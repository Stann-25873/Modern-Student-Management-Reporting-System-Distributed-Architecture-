package com.server.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.Properties;
import javax.mail.internet.*;
import javax.mail.PasswordAuthentication;

public class NotificationConsumer implements MessageListener, Runnable {

    private final String brokerUrl = "tcp://localhost:61616";
    private final String queueName = "REGISTRATION_OTP_QUEUE";

    // Gmail Credentials - Replace with your actual App Password
    private final String myEmail = "stanikam2@gmail.com"; 
    private final String appPassword = "vkjbwsqnxlszouaw"; 

    @Override
    public void run() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            // Using JMS Session
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);

            consumer.setMessageListener(this);
            System.out.println(">>> [ActiveMQ] Listener is ACTIVE and waiting for messages.");
        } catch (Exception e) {
            System.err.println(">>> [ActiveMQ] Start Error: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage map = (MapMessage) message;
                String email = map.getString("email");
                String otp = map.getString("otp");
                
                System.out.println(">>> [ActiveMQ] Processing OTP for: " + email);
                sendRealEmail(email, otp);
            }
        } catch (Exception e) {
            System.err.println(">>> [ActiveMQ] Message Processing Error: " + e.getMessage());
        }
    }

    private void sendRealEmail(String recipient, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.timeout", "5000"); // 5-second timeout
        props.put("mail.smtp.connectiontimeout", "5000");

        // Create JavaMail Session
        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, appPassword);
            }
        });

        try {
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Verification Code: " + otp);
            
            String content = "Hello,\n\n"
                           + "Your secure verification code is: " + otp + "\n\n"
                           + "Please enter this code in the Student Registration System to verify your identity.\n"
                           + "If you did not request this code, please ignore this email.\n\n"
                           + "Best regards,\n"
                           + "Student System Admin";
            
            message.setText(content);

            // Send the email
            javax.mail.Transport.send(message);
            System.out.println(">>> [SUCCESS] Gmail sent OTP [" + otp + "] to: " + recipient);

        } catch (Exception e) {
            System.err.println(">>> [ERROR] SMTP Failure (Check your App Password or Internet): " + e.getMessage());
            // Important fallback: Print OTP to console so you can still log in while debugging
            System.out.println(">>> [FALLBACK] Use this code manually to bypass: " + otp);
        }
    }
}