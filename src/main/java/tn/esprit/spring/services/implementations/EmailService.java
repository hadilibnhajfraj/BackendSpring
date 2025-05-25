package tn.esprit.spring.services.implementations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.RandomStringUtils;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendTemporaryPassword(String to) {
        String temporaryPassword = RandomStringUtils.random(12, true, true); // Génère un mot de passe aléatoire sécurisé

        String subject = "Votre mot de passe temporaire";
        String text = "Voici votre mot de passe temporaire : " + temporaryPassword + "\n" +
                "Utilisez-le pour vous connecter, puis changez-le immédiatement.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        return temporaryPassword;
    }
    public void sendPasswordResetEmail(String to, String tempPassword) {
        String subject = "Votre mot de passe temporaire";
        String text = "Voici votre mot de passe temporaire : " + tempPassword +
                "\nVeuillez l'utiliser pour réinitialiser votre mot de passe.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


}
