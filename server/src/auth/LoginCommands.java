package server.src.auth;

import server.src.DBManager;
import server.src.MailSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public enum LoginCommands {
    HELP("help")
            {
                @Override
                protected boolean executeCommand(List<String> args, LoginUserThread userThread)
                {
                    if (args.size() == 0)
                    {
                        userThread.sendln("There are " + LoginCommands.values().length + " available commands:");

                        for (LoginCommands c : LoginCommands.values())
                            userThread.sendln(c.getUsage());
                    }
                    else if (args.size() == 1)
                    {
                        LoginCommands c = LoginCommands.findCommand(args.get(0));
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
    REGISTER("register")
            {
                @Override
                protected boolean executeCommand(List<String> args, LoginUserThread userThread)
                {
                    if (args.size() == 1)
                    {
                        String login = args.get(0);

                        if (!EMAIL_PATTERN.matcher(login).matches())
                        {
                            userThread.sendln("Please enter a valid email address");
                        }
                        else if (!DBManager.checkIfUserExists(login))
                        {
                            MailSender mail = new MailSender();
                            String password = mail.sendPassword(login);
                            DBManager.insertUser(login, password);

                            userThread.sendln("Successfully added a new user. Please check your mail for a password.");
                        }
                        else
                            userThread.sendln("User with this e-mail already exists");

                        return true;
                    }
                    return true;
                }

                @Override
                protected String getUsage() {
                    return "register <e-mail>";
                }
            },
    LOGIN("login")
            {
                @Override
                protected boolean executeCommand(List<String> args, LoginUserThread userThread)
                {
                    if (args.size() == 2)
                    {
                        String login = args.get(0);
                        String password = args.get(1);

                        if (!EMAIL_PATTERN.matcher(login).matches())
                        {
                            userThread.sendln("Please enter a valid email address");
                        }
                        if (DBManager.checkSignIn(login, password))
                            userThread.setLogin(login);
                        else
                            userThread.sendln("Incorrect login information");

                        return true;
                    }
                    return false;
                }

                @Override
                protected String getUsage() {
                    return "login <e-mail> <password>";
                }
            },
    EXIT("exit")
            {
                @Override
                protected boolean executeCommand(List<String> args, LoginUserThread userThread)
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

    public static void execute(String input, LoginUserThread userThread)
    {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(input.split(" ")));
        String command = args.get(0);
        args.remove(command);

        LoginCommands c = findCommand(command);
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
            userThread.sendln("Please log in to the system. Use \"help\" to get available commands");
    }

    public static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    protected String commandName;

    LoginCommands(String command)
    {
        commandName = command;
    }

    private static LoginCommands findCommand(String name)
    {
        for (LoginCommands c : LoginCommands.values()) {
            if (c.commandName.equals(name)) {
                return c;
            }
        }
        return null;
    }

    //Should return False is the usage of the command is incorrect
    protected abstract boolean executeCommand(List<String> args, LoginUserThread userThread);

    protected abstract String getUsage();
}
