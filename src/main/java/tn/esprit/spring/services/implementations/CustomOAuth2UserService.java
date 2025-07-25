package tn.esprit.spring.services.implementations;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession; // Important pour la session
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extraire les attributs de Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name"); // Nom complet
        String givenName = oAuth2User.getAttribute("given_name"); // Prénom
        String familyName = oAuth2User.getAttribute("family_name"); // Nom de famille
        // Note: Google OAuth2 de base ne fournit PAS la date de naissance.
        // Vous devrez l'obtenir via l'API Google People ou la demander à l'utilisateur.
        // Pour cet exemple, nous simulons une absence de date de naissance.
        String birthdayStr = null; // oAuth2User.getAttribute("birthday"); // Probablement null

        System.out.println("Attributs OAuth2 reçus de Google :");
        System.out.println("  Email: " + email);
        System.out.println("  Name: " + name);
        System.out.println("  Given Name: " + givenName);
        System.out.println("  Family Name: " + familyName);
        System.out.println("  Birthday (simulé): " + birthdayStr);

        // Préparer les données utilisateur à stocker temporairement
        Map<String, Object> tempUserData = new HashMap<>();
        tempUserData.put("email", email);

        // Déterminer Prénom et Nom
        if (givenName != null && !givenName.isEmpty()) {
            tempUserData.put("prenom", givenName);
        } else if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ", 2);
            tempUserData.put("prenom", parts[0]);
        } else {
            tempUserData.put("prenom", ""); // Par défaut
        }

        if (familyName != null && !familyName.isEmpty()) {
            tempUserData.put("nom", familyName);
        } else if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ", 2);
            if (parts.length > 1) {
                tempUserData.put("nom", parts[1]);
            } else {
                tempUserData.put("nom", ""); // Par défaut
            }
        } else {
            tempUserData.put("nom", ""); // Par défaut
        }

        // Gérer la date de naissance (simulée comme absente pour Google OAuth2 standard)
        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(birthdayStr, DateTimeFormatter.ISO_LOCAL_DATE);
                tempUserData.put("dateNaissance", birthDate);
            } catch (DateTimeParseException e) {
                System.err.println("Impossible de parser la date de naissance : " + birthdayStr);
                tempUserData.put("dateNaissance", null); // Ou une date par défaut
            }
        } else {
            tempUserData.put("dateNaissance", null); // Indiquer qu'elle n'est pas disponible
        }

        // Stocker les données dans la session HTTP
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true); // Crée la session si elle n'existe pas
        session.setAttribute("tempOAuth2User", tempUserData);
        session.setAttribute("oauth2UserAttributes", oAuth2User.getAttributes()); // Conserver les attributs complets si besoin

        System.out.println("Données utilisateur OAuth2 stockées temporairement dans la session pour " + email);

        // Retourner l'OAuth2User original. L'utilisateur sera redirigé par le successHandler.
        return oAuth2User;
    }
}