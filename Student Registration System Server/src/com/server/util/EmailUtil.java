package com.server.util; // Ensure this matches your package structure

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {
    public static void sendEmail(String to, String subject, String body) {
        // 1. YOUR GMAIL ADDRESS
        final String from = "stanikam2@gmail.com"; 
        
        // 2. THE 16-CHARACTER APP PASSWORD (from Google Security)
        final String password = "abcd efgh ijkl mnop"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println(">>> [SUCCESS] Email sent to " + to);
        } catch (MessagingException e) {
            System.out.println(">>> [ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}