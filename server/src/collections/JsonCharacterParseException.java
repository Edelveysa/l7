package server.src.collections;

public class JsonCharacterParseException extends RuntimeException
{

    public JsonCharacterParseException()
    {
        super();
    }

    public JsonCharacterParseException(String message)
    {
        super(message);
    }
}