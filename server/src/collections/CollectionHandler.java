package server.src.collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.src.DBManager;
import server.src.UserThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

public class CollectionHandler
{
    private File file;
    public ConcurrentSkipListSet<Character> characters;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private LocalDate modificationDate;
    private LocalTime modificationTime;


    public CollectionHandler()
    {
        characters = new ConcurrentSkipListSet<>();
        creationTime = modificationTime = LocalTime.now();
        creationDate = modificationDate = LocalDate.now();
    }


    //Getters

    public int getLength()
    {
        return characters.size();
    }

    public String getJsonFromCollection()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(characters);
    }


    // Modification

    public void addCharacter(String json, UserThread userThread)
    {
        addCharacter(getCharacterFromJson(json), userThread);
    }

    public void addCharacter(Character c, UserThread userThread)
    {
        if(characters.add(c))
            collectionUpdated();
        else
            userThread.sendln("Character with that name already exists");
    }

    public void removeCharacter(String json, UserThread userThread)
    {
        removeCharacter(getCharacterFromJson(json), userThread);
    }

    public void removeCharacter(Character c, UserThread userThread)
    {
        if(characters.remove(c))
            collectionUpdated();
        else
            userThread.sendln("No such character was found");
    }


    // Saving and loading

    public void loadFromFile(String filePath, UserThread userThread)
    {
        try
        {
            characters.clear();

            file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String jsonString = "";
            while ((line = bufferedReader.readLine()) != null)
                jsonString += line;

            jsonImport(jsonString);

            creationDate = LocalDate.now();
            creationTime = LocalTime.now();

            userThread.sendln("Collection successfully loaded");
        }
        catch (Exception e)
        {
            userThread.sendln("Loading failed: " + e.getMessage());
        }
    }

    public void loadFromJson(String json, UserThread userThread)
    {
        try
        {

            jsonImport(json);

            creationDate = LocalDate.now();
            creationTime = LocalTime.now();

            userThread.sendln("Collection successfully imported");
        }
        catch (Exception e)
        {
            userThread.sendln("Importing failed: " + e.getMessage());
        }
    }

    public void loadFromDB()
    {
        characters.addAll(DBManager.receiveCollection());
    }

    private void jsonImport(String json)
    {
        Gson gson = new Gson();
        Character[] characterArray = gson.fromJson(json, Character[].class);

        characters.addAll(Arrays.asList(characterArray));
    }

    public void saveToFile(String filePath, UserThread userThread)
    {
        file = new File(filePath);
        saveToFile(userThread);
    }

    public void saveToFile(UserThread userThread)
    {
        if (file == null)
            throw new RuntimeException("File is not initialised");

        String json = getJsonFromCollection();

        String[] jsonLines = json.split("\n");

        try {
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);

            for (String line : jsonLines) {
                writer.println(line);
            }

            writer.close();

            if (userThread != null)
                userThread.sendln("Collection successfully saved");
        }
        catch (Exception e)
        {
            if (userThread != null)
                userThread.sendln("Saving failed: " + e.getMessage());
            System.out.println("Saving failed: " + e.getMessage());
        }
    }


    // Returning information

    public int compareToMax(String json)
    {
        return getCharacterFromJson(json).compareTo(characters.last());
    }

    public Character getCharacter(String name)
    {
        for (Character c : characters)
            if (c.getName().equals(name))
                return c;

        return null;
    }

    // Printing information

    public void printElements(UserThread userThread)
    {
        userThread.sendln("Current collection elements:");

        Object[] array = characters.toArray();
        Stream.iterate(1, x -> x + 1)
                .limit(array.length)
                .map(x -> x + ": " + array[x - 1].toString())
                .forEach(userThread::sendln);

        if (array.length == 0)
            userThread.sendln("(empty)");
    }

    public void printInfo(UserThread userThread)
    {
        userThread.sendln("Element count: " + getLength());
        userThread.sendln("First population: " + creationDate + " " + creationTime);
        userThread.sendln("Last modification: " + modificationDate + " " + modificationTime);
    }


    // Utility

    public static Character getCharacterFromJson(String json)
    {
        Gson gson = new Gson();
        Character c = gson.fromJson(json, Character.class);
        c.initAfterJson();
        return c;
    }

    private void collectionUpdated()
    {
        modificationTime = LocalTime.now();
        modificationDate = LocalDate.now();
    }
}