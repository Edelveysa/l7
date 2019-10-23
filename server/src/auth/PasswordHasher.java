package server.src.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


public class PasswordHasher
{
    public static String hash(String password)
    {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(md.digest());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}