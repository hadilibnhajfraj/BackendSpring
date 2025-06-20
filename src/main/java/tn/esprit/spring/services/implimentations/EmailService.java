package tn.esprit.spring.services.implimentations;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Evenement;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Reservation;
import tn.esprit.spring.entities.Terrain;

import java.text.SimpleDateFormat;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.frontend.base-url}") // Frontend URL, e.g. https://your-frontend.com
    private String frontendBaseUrl;

    public void sendConfirmationEmail(Reservation reservation) throws MessagingException {
        Evenement evenement = reservation.getEvenement();
        MatchFo match = evenement.getMatch();
        Terrain terrain = match.getTerrain();

        String nomEquipe1 = match.getEquipes1().get(0).getNom();
        String nomEquipe2 = match.getEquipes1().get(1).getNom();

        // Single URL to frontend confirmation page with id and email params
        String confirmationPageUrl = frontendBaseUrl + "/confirmation?id=" + reservation.getId()
                + "&email=" + reservation.getEmail();

        String dateHeureMatch = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(match.getHeureMatch());

        String subject = "Action requise : Confirmation de votre réservation";

        String htmlMsg = """
        <html>
        <body>
            <p>Bonjour %s %s,</p>

            <p>Vous avez réservé <b>%d place(s)</b> pour l'événement suivant :</p>

            <ul>
                <li><b>Match :</b> %s</li>
                <li><b>Équipes :</b> %s VS %s</li>
                <li><b>Terrain :</b> %s</li>
                <li><b>Adresse :</b> %s</li>
                <li><b>Date et heure :</b> %s</li>
            </ul>

            <p>Pour confirmer ou refuser votre réservation, veuillez cliquer sur le bouton ci-dessous :</p>

            <p>
                <a href="%s" style="display:inline-block; padding:12px 25px; font-size:16px; color:white; background-color:#007bff; text-decoration:none; border-radius:5px;">
                    Gérer ma réservation
                </a>
            </p>

            <p>Merci.</p>
        </body>
        </html>
        """.formatted(
                reservation.getPrenom(),
                reservation.getNom(),
                reservation.getNombrePlaces(),
                match.getNom(),
                nomEquipe1,
                nomEquipe2,
                terrain.getNom(),
                terrain.getAdresse(),
                dateHeureMatch,
                confirmationPageUrl
        );

        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
        helper.setTo(reservation.getEmail());
        helper.setSubject(subject);
        helper.setText(htmlMsg, true);  // true = HTML

        javaMailSender.send(mail);
    }

    // Optional: simple emails for confirmed/refused notifications
    public void sendConfirmationMail(Reservation reservation) {
        String subject = "Confirmation de votre réservation";
        String message = String.format("Bonjour %s %s,\n\nVotre réservation pour l'événement %s a été confirmée.\n\nMerci.",
                reservation.getPrenom(), reservation.getNom(), reservation.getEvenement().getTerrain().getNom());

        sendEmail(reservation.getEmail(), subject, message);
    }

    public void sendRefusalMail(Reservation reservation) {
        String subject = "Refus de votre réservation";
        String message = String.format("Bonjour %s %s,\n\nVotre réservation pour l'événement %s a été refusée.\n\nMerci de votre compréhension.",
                reservation.getPrenom(), reservation.getNom(), reservation.getEvenement().getTerrain().getNom());

        sendEmail(reservation.getEmail(), subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // plain text
            javaMailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
            // handle error or throw a runtime exception if necessary
        }
    }
}
