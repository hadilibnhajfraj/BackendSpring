package tn.esprit.spring.services.implementations;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    // Inject the JavaMailSender bean using constructor injection
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // Method to send a simple email
    public void sendEmail(String to, String subject, String body) throws MessagingException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Method to send a simple email (without HTML content)
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        javaMailSender.send(message);
    }

    // Method to generate a temporary password and send it via email
    public String sendTemporaryPassword(String to) throws MessagingException {
        String tempPassword = generateTempPassword();
        sendEmail(to, "Temporary Password", "Your temporary password is: " + tempPassword);
        return tempPassword;
    }

    // Helper method to generate a random temporary password
    private String generateTempPassword() {
        int length = 12;  // Length of the temporary password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        return password.toString();
    }
}
