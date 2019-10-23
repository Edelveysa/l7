package server.src;

import server.src.collections.CollectionHandler;
import server.src.collections.Commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class UserThread extends Thread
{
    private Socket socket;
    private String alias;
    private boolean connected;

    private DataInputStream in;
    private DataOutputStream out;

    private CollectionHandler collection;


    public UserThread(Socket s, String alias)
    {
        socket = s;
        this.alias = alias;

        System.out.println(alias + "successfully logged in");

        connected = setupDataStreams();

        collection = Main.getCollection();
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

    @Override
    public void run()
    {
        try
        {
            while (connected)
            {
                String input = in.readUTF();
                System.out.println(alias + " > " + input);

                Commands.execute(input, this);
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

        //System.out.println("Saving the collection of user " + alias);
        collection.saveToFile("backup.json",this);
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

    public CollectionHandler getCollection()
    {
        return collection;
    }

    public String getAlias()
    {
        return alias;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public DataInputStream getInputStream()
    {
        return in;
    }

    public  DataOutputStream getOutputStream()
    {
        return out;
    }

    public void setConnectedStatus(boolean status)
    {
        connected = status;
    }
}
