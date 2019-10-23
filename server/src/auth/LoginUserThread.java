package server.src.auth;


import server.src.UserThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class LoginUserThread extends Thread
{
    private Socket socket;
    private String alias;
    private boolean connected;
    private boolean loggedIn;

    private DataInputStream in;
    private DataOutputStream out;

    public LoginUserThread(Socket s)
    {
        socket = s;
        alias = socket.getInetAddress().toString();

        System.out.println("Established a connection with " + alias);

        connected = setupDataStreams();
        loggedIn = false;
    }

    private boolean setupDataStreams()
    {
        try
        {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            return true;
        }
        catch (IOException e)
        {
            System.out.println("[ERROR] Could not initialise data streams");
            e.printStackTrace();
            return false;
        }
    }


    public void run()
    {
        try
        {
            while (connected)
            {
                String input = in.readUTF();
                System.out.println(alias + " > " + input);

                LoginCommands.execute(input, this);

                if (loggedIn)
                    return;
            }
        }
        catch (SocketException e)
        {
            connected = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            connected = false;
        }
        finally
        {
            System.out.println("Connection with " + alias + " has been lost");
        }
    }


    public void sendln(String message)
    {
        if (connected) {
            try {
                out.writeUTF(message);
            } catch (Exception e) {
                System.out.println("[ERROR] Error sending a message to " + alias + ": " + e.getMessage());
                connected = false;
            }
        }
    }

    public void setLogin(String login)
    {
        UserThread userThread = new UserThread(socket, login);
        userThread.start();

        loggedIn = true;
    }

    public void setConnectedStatus(boolean status)
    {
        connected = status;
    }

}
