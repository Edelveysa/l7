package server.src;

import server.src.collections.Character;
import server.src.auth.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {

    //Данные значения для моей локальной PostgreSQL БД, их надо заменить на закоменченные при заливе на helios.
    private static final String DBURL = "jdbc:postgresql://localhost:1112/studs"; //"jdbc:postgresql://pg:5432/studs"
    private static final String DBLOGIN = "postgres";  // Helios login
    private static final String DBPASSWORD = "midorima16"; // Helios password

    // Вставляет персонажа в БД.
    public static void insertCharacter(Character character) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM Characters7 WHERE name = ?");
                stmt.setString(1, character.getName());
                ResultSet result = stmt.executeQuery();
                if (result.next())
                    System.out.println("Объект уже существует");
                else {
                    stmt = con.prepareStatement("INSERT INTO Characters7 (name, description, power, location, creation_moment, owner) VALUES (?, ?, ?, ?, ?, ?)");
                    stmt.setString(1, character.getName());
                    stmt.setString(2, character.getDescription());
                    stmt.setInt(3, character.getPower());
                    stmt.setString(4, character.getSpacedLocation());
                    stmt.setString(5, character.getCreationMoment().toString());
                    stmt.setString(6, character.getOwner());
                    stmt.executeUpdate();
                }
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Удаляет персонажа из БД.
    public static void deleteCharacter(Character character) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM Characters7 WHERE name = ?");
                stmt.setString(1, character.getName());
                stmt.executeUpdate();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Вставляет пользователя с паролем в БД.
    public static void insertUser(String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                if (checkIfUserExists(username))
                    System.out.println("Пользователь уже существует");
                else {
                    String passwordHash = PasswordHasher.hash(password);
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO Users (username, password) VALUES (?, ?)");
                    stmt.setString(1, username);
                    stmt.setString(2, passwordHash);
                    stmt.executeUpdate();
                    stmt.close();
                }
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Проверяет, есть ли в БД такие username и password.
    public static boolean checkSignIn(String username, String password) {
        boolean check = false;
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                String passwordHash = PasswordHasher.hash(password);
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, passwordHash);
                ResultSet result = stmt.executeQuery();
                check = result.next();
                result.close();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    //Проверяет, есть ли в БД такой юзер.
    public static boolean checkIfUserExists(String username) {
        boolean check = false;
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM Users WHERE username = '" + username + "'");
                check = result.next();
                result.close();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    public static ArrayList<Character> receiveCollection() {
        ArrayList<Character> collection = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(DBURL, DBLOGIN, DBPASSWORD);
            try {
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM Characters7");
                while (result.next())
                    collection.add(new Character(
                            result.getString(1),
                            result.getString(2),
                            result.getInt(3),
                            result.getString(4),
                            result.getString(5),
                            result.getString(6)));
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }
}