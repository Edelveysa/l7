package client;

import info.NetworkInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public final class Main
{
    // Networking
    private static Socket socket;
    private static boolean connectionEstablished = false;
    public static boolean exitRequested = false;
    public static String fileToSend = null;

    // Data
    private static DataOutputStream out;
    private static DataInputStream in;
    private static MessageReadThread messageReadThread;

    // User Input
    private static Scanner reader;


    static public void main(String[] args)
    {
        reader = new Scanner(System.in);
        socket = new Socket();
        InetAddress address = null;
        int port = NetworkInfo.defaultPort;


        // Trying to use address and port from input, if failing - using the defaults
        try
        {
            address = InetAddress.getByName(NetworkInfo.defaultAddress);
            address = InetAddress.getByName(args[0]);
        }
        catch (Exception e){
            System.out.println("Using the default address");
        }

        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (Exception e){
            System.out.println("Using the default port");
        }


        // Main loop
        while (true)
        {
            if (!connectionEstablished)
                if (!connect(address, port, 10))
                    break;

            readInputAndSendToServer();

            if (fileToSend != null)
                sendFileToServer();

            if (exitRequested)
            {
                break;
            }
        }

        reader.close();
    }

    private static boolean connect(InetAddress address, int port, int attempts)
    {
        System.out.println("Connecting to " + address + " on port " + port);

        while (!connectionEstablished)
        {
            --attempts;
            if (attempts <= 0)
            {
                System.out.println();
                System.out.println("Server is currently unavailable. " +
                        "Check the connection data or try again later.");
                return false;
            }

            attemptConnection(address, port);

            System.out.print(" .");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println(" connection established.");
        return true;
    }

    private static void attemptConnection(InetAddress address, int port)
    {
        try
        {
            socket.close();
            socket = new Socket(address, port);

            //If no exceptions were thrown, we have successfully connected
            connectionEstablished = true;

            // Initialising input and output streams
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // Start waiting for the server messages
            messageReadThread = new MessageReadThread();
            messageReadThread.start();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    private static void readInputAndSendToServer()
    {
        try
        {
            String line = reader.nextLine();
            line = ClientCommands.modifyMessageIfCommandEntered(line);
            if (line != null) {
                System.out.println("Request sent");
                out.writeUTF(line);
            }
        }
        catch (SocketException e)
        {
            System.out.println("Lost connection to server");
            connectionEstablished = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void sendFileToServer()
    {
        try
        {
            Thread.sleep(1000);

            File file = new File(fileToSend);
            long fileSize = file.length();
            System.out.println("Sending file of size " + fileSize + " to a server now");

            FileInputStream fis = new FileInputStream(file);

            out.writeLong(fileSize);

            Thread.sleep(1000);

            byte[] buffer = new byte[NetworkInfo.fileSendBufferSize];

            int length;
            while ((length = fis.read(buffer)) != -1)
            {
                out.write(buffer, 0, length);
            }
        }
        catch (Exception e)
        {
            System.out.println("[ERROR] Error sending file to server: " + e.getMessage());
        }
        fileToSend = null;
    }

    private static class MessageReadThread extends Thread
    {
        @Override
        public void run() {
            while (connectionEstablished)
            {
                try
                {
                    System.out.println(in.readUTF());
                }
                catch (Exception e)
                {
                    connectionEstablished = false;
                }
            }
        }
    }
}