package server.src.collections;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Character implements Comparable<Character>, Serializable
{
    private String name;
    private String description;
    private int power;
    private Location location;
    private OffsetDateTime creationMoment;
    private String owner;

    public Character(String name)
    {
        this.name = name;
        initAfterJson();
    }

    public Character(String name, String description, int power,
                     Location location, OffsetDateTime creationMoment, String owner)
    {
        this.name = name;
        this.description = description;
        this.power = power;
        this.location = location;
        this.creationMoment = creationMoment;
        this.owner = owner;
    }

    // Вызывает предедущий конструктор, но принимает строковые переменные вместо
    // локации и времени\даты
    public Character(String name, String description, int power,
                     String spacedLocation, String creationString, String owner)
    {
        this(
                name,
                description,
                power,
                new Location(spacedLocation),
                OffsetDateTime.parse(creationString),
                owner);
    }

    public void initAfterJson()
    {
        if (name == null)
            throw new JsonCharacterParseException("Variable \"name\" is required");

        if (owner != null)
            throw new JsonCharacterParseException("Variable \"owner\" should not be assigned manually");

        if (description == null)
            description = "A character";

        if (location == null)
            location = new Location(0, 100, 0);

        if (creationMoment == null)
            creationMoment = OffsetDateTime.now();
    }


    @Override
    public int compareTo(Character o)
    {
        return name.compareTo(o.name);
    }

    @Override
    public String toString()
    {
        // Formatting date of birth
        String creationMomentString = creationMoment.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return name + " - " + description +
                "; Birth date - " + creationMomentString +
                "; Power - " + power +
                "; Location - " + location;
    }


    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getPower(){
        return this.power;
    }

    public String getSpacedLocation(){
        return this.location.toSpacedString();
    }

    public OffsetDateTime getCreationMoment(){
        return creationMoment;
    }

    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner)
    {
        this.owner = owner;
    }


    public static class Location
    {
        public float x;
        public float y;
        public float z;

        public Location(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Location(String spacedLocation)
        {
            String[] nums = spacedLocation.split(" ");
            this.x = Float.parseFloat(nums[0]);
            this.y = Float.parseFloat(nums[1]);
            this.z = Float.parseFloat(nums[2]);
        }

        @Override
        public String toString() {
            return String.format("[%.1f; %.1f; %.1f]", x, y, z);
        }

        public String toSpacedString()
        {
            return x + " " + y + " " + z;
        }
    }
}