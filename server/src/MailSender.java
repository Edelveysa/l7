package server.src;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class MailSender {

    private String username = "seventh.laba@gmail.com";
    private String password = "YHVTHFiw78QTRep";
    private Properties props;

    public MailSender() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    //Метод отправит сгенерированный пароль на указанну почту, возвращает сгенерированный пароль в виде строки.
    public String sendPassword(String toEmail)
    {
        // Генерация пароля
        String generatedPassword = new Random()
                .ints(5, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try
        {
            Message message = new MimeMessage(session);
            //от кого
            message.setFrom(new InternetAddress(username));
            //кому
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            //Заголовок письма
            message.setSubject("Регистрация в сервисе 'Седьмая лаба по проге'");
            //Содержимое
            message.setText("Благодарим за регистрацию в нашем сервисе!\n" +
                    "Пароль от вашего аккаунта: " + generatedPassword +
                    "\nНе теряйте ваш пароль, не передавайте его другим блаблабла.");

            //Отправляем сообщение
            Transport.send(message);
        }
        catch (MessagingException e)
        {
            System.out.println("Ошибка при отправке сообщения на почту.");
        }

        return generatedPassword;
    }
}
