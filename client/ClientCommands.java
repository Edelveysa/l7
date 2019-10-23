package client;

import info.CommandParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public enum ClientCommands {
    /*
        IMPORT("import")
                {
                    @Override
                    protected String executeCommandAndModifyOutput(List<String> args, String originalInput)
                    {
                        if (args.size() == 1)
                        {
                            try
                            {
                                File file = new File(args.get(0));
                                if (file.exists())
                                {
                                    Main.fileToSend = args.get(0);
                                    return "import -await " + file.getName();
                                }
                                else
                                {
                                    System.out.println("File doesn't exist");
                                    return null;
                                }
                            }
                            catch (Exception e)
                            {
                                System.out.println("Error loading from file");
                                return null;
                            }
                        }
                        return originalInput;
                    }
                },
     */
    EXIT("exit")
            {
                @Override
                protected String executeCommandAndModifyOutput(List<String> args, String originalInput)
                {
                    Main.exitRequested = true;
                    System.out.println("Sending an exit message to server and closing up.");
                    return originalInput;
                }
            };

    public static String modifyMessageIfCommandEntered(String input)
    {
        List<String> args = CommandParser.parse(input);
        String command = args.get(0);
        args.remove(command);

        ClientCommands c = findCommand(command);
        if (c != null)
            return c.executeCommandAndModifyOutput(args, input);
        else
            return input;
    }


    protected String commandName;

    ClientCommands(String command)
    {
        commandName = command;
    }

    private static ClientCommands findCommand(String name)
    {
        for (ClientCommands c : ClientCommands.values()) {
            if (c.commandName.equals(name)) {
                return c;
            }
        }
        return null;
    }

    //Should return False is the usage of the command is incorrect
    protected abstract String executeCommandAndModifyOutput(List<String> args, String originalInput);
}