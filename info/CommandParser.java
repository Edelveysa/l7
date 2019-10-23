package info;


import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    public static List<String> parse(String input)
    {
        ArrayList<String> parsedInput = new ArrayList<>();

        String[] split = input.split(" ", 2); // Отделяем команду от потенциальных аргументов
        parsedInput.add(split[0]);            // И добавляем первый элемент в коллекцию

        while (split.length == 2 && split[1].startsWith("-"))
        {
            // Пока есть что-то ещё в вводе, и оно начинается с "-" (т.е. есть ещё аргументы)
            // Мы отделяем их друг от друга и добавляем по одному в вывод. Повторяем, пока есть аргументы.
            split = split[1].split(" ", 2);
            parsedInput.add(split[0]);
        }

        if (split.length == 2)
            parsedInput.add(split[1]); // Прибавляем, что осталось

        return parsedInput;
    }
}

