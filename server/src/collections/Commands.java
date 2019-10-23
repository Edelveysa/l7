package server.src.collections;

import com.google.gson.JsonSyntaxException;
import info.CommandParser;
import server.src.DBManager;
import server.src.UserThread;


import java.util.List;

public enum Commands {
    HELP("help")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread)
                {
                    if (args.size() == 0)
                    {

                        userThread.sendln("There are " + Commands.values().length + " available commands:");

                        for (Commands c : Commands.values())
                            userThread.sendln(c.getUsage());
                    }
                    else if (args.size() == 1)
                    {
                        Commands c = Commands.findCommand(args.get(0));
                        if (c != null)
                            userThread.sendln(c.getUsage());
                        else
                            userThread.sendln("There is no such command as \"" + args.get(0) + "\"");
                    }
                    else
                        return false;

                    return true;
                }

                @Override
                protected String getUsage() {
                    return "help\n"
                            + "help <command>";
                }
            },
    INFO("info")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0) {
                        userThread.getCollection().printInfo(userThread);
                        return true;
                    }
                    else return false;
                }

                @Override
                protected String getUsage() {
                    return "info";
                }
            },
    SHOW("show")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 0) {
                        userThread.getCollection().printElements(userThread);
                        return true;
                    }
                    else if ((args.size() == 1) && (args.contains("-json")))
                    {
                        userThread.sendln(userThread.getCollection().getJsonFromCollection());
                        return true;
                    }
                    else return false;
                }

                @Override
                protected String getUsage() {
                    return "show\n"
                            + "show -json";
                }
            },
    ADD("add")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 1)
                    {
                        addElement(args.get(0), userThread);
                        return true;
                    }
                    else if (args.size() == 2 && args.contains("-ifmax"))
                    {
                        if (userThread.getCollection().compareToMax(args.get(1)) > 0)
                            addElement(args.get(1), userThread);
                        else
                            userThread.sendln("Element is not bigger than "
                                    + "the existing ones and was not added");
                        return true;
                    }
                    return false;
                }

                private void addElement(String json, UserThread userThread)
                {
                    try {
                        Character c = CollectionHandler.getCharacterFromJson(json);
                        c.setOwner(userThread.getAlias()); // Присвоение хозяина

                        userThread.getCollection().addCharacter(c, userThread);
                        DBManager.insertCharacter(c);

                        userThread.sendln("Element added successfully");
                    }
                    catch (JsonSyntaxException | JsonCharacterParseException e)
                    {
                        userThread.sendln("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected String getUsage() {
                    return "add <element>\n"
                            + "add -ifmax <element>";
                }
            },
    REMOVE("remove")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread) {
                    if (args.size() == 1)
                    {
                        removeElement(args.get(0),userThread);
                        return true;
                    }
                    return false;
                }

                private void removeElement(String name, UserThread userThread)
                {
                    try {
                        Character c = userThread.getCollection().getCharacter(name);

                        if (c == null)
                            userThread.sendln("No character with that name was found");
                        else if (c.getOwner().equals(userThread.getAlias())) // Проверка на хозяина
                        {
                            userThread.getCollection().removeCharacter(name, userThread);
                            DBManager.deleteCharacter(c);

                            userThread.sendln("Element removed successfully");
                        }
                        else
                            userThread.sendln("This element does not belong to you");
                    }
                    catch (JsonSyntaxException | JsonCharacterParseException e) {
                        userThread.sendln("Incorrect JSON syntax: " + e.getMessage());
                    }
                }

                @Override
                protected String getUsage() {
                    return "remove <name>";
                }
            },
    /*
        LOAD("load")
                {
                    @Override
                    protected boolean executeCommand(List<String> args, UserThread userThread) {
                        if (args.size() == 1)
                        {
                            userThread.getCollection().loadFromFile(args.get(0), userThread);
                            return true;
                        }
                        return false;
                    }
                    @Override
                    protected String getUsage() {
                        return "load";
                    }
                },
        SAVE("save")
                {
                    @Override
                    protected boolean executeCommand(List<String> args, UserThread userThread) {
                        if (args.size() == 1)
                        {
                            userThread.getCollection().saveToFile(args.get(0), userThread);
                            return true;
                        }
                        return false;
                    }
                    @Override
                    protected String getUsage() {
                        return "save";
                    }
                },
        IMPORT("import")
                {
                    @Override
                    protected boolean executeCommand(List<String> args, UserThread userThread)
                    {
                        if (args.size() == 2 && args.get(0).equals("-await"))
                        {
                            try
                            {
                                File file = new File(args.get(1));
                                while (!file.createNewFile()) {
                                    file = new File("_" + file.getName());
                                }
                                DataInputStream in = userThread.getInputStream();
                                long size = in.readLong();
                                byte[] buffer = new byte[NetworkInfo.fileSendBufferSize];
                                BufferedInputStream bis = new BufferedInputStream(in);
                                FileOutputStream fos = new FileOutputStream(file);
                                System.out.println("Waiting for file of size " + size + " now");
                                int length;
                                while (size > 0) {
                                    length = bis.read(buffer);
                                    fos.write(buffer, 0, length);
                                    size -= length;
                                }
                                System.out.println("File " + file.getName() + " successfully imported");
                                userThread.sendln("File " + file.getName() + " successfully imported");
                                fos.close();
                            } catch (Exception e) {
                                userThread.sendln("Error accepting file: " + e.getMessage());
                            }
                            return true;
                        }
                        return false;
                    }
                    @Override
                    protected String getUsage() {
                        return "import <file path>";
                    }
                },
    */
    EXIT("exit")
            {
                @Override
                protected boolean executeCommand(List<String> args, UserThread userThread)
                {
                    userThread.setConnectedStatus(false);
                    return true;
                }

                @Override
                protected String getUsage() {
                    return "exit";
                }
            }
    ;

    public static void execute(String input, UserThread userThread)
    {
        List<String> args = CommandParser.parse(input);
        String command = args.get(0);
        args.remove(command);

        Commands c = findCommand(command);
        if (c != null)
        {
            boolean usage = c.executeCommand(args, userThread);
            if (!usage)
            {
                userThread.sendln("Correct usage:");
                userThread.sendln(c.getUsage());
            }
        }
        else
            userThread.sendln("Unknown command. Use \"help\" to see the list of commands");
    }


    protected String commandName;

    Commands(String command)
    {
        commandName = command;
    }

    private static Commands findCommand(String name)
    {
        for (Commands c : Commands.values()) {
            if (c.commandName.equals(name)) {
                return c;
            }
        }
        return null;
    }

    //Should return False is the usage of the command is incorrect
    protected abstract boolean executeCommand(List<String> args, UserThread userThread);

    protected abstract String getUsage();
}
