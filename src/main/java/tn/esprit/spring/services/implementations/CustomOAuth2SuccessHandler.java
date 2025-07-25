package tn.esprit.spring.services.implementations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

   /* @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Rediriger vers une page spéciale du frontend pour choisir le rôle
        // Cette page aura accès aux données utilisateur stockées dans la session
        response.sendRedirect("http://localhost:4200/select-role"); // <-- Nouvelle URL
        // Note: L'utilisateur est techniquement authentifié par Spring Security OAuth2 ici,
        // mais son compte n'est pas encore créé dans votre table 'user'.
        // La création finale se fera après avoir choisi le rôle dans le frontend.
    }*/
   @Override
   public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication)
           throws IOException, ServletException {

       var oauthToken = (OAuth2AuthenticationToken) authentication;
       var oauthUser = oauthToken.getPrincipal();
       var attributes = oauthUser.getAttributes();

       // Extract user data (adjust keys for Facebook)
       String email = (String) attributes.get("email");
       String name = (String) attributes.get("name"); // Facebook returns full name

       String prenom = "";
       String nom = "";

       if (name != null) {
           String[] parts = name.split(" ", 2);
           prenom = parts[0];
           nom = parts.length > 1 ? parts[1] : "";
       }

       // Store in session
       Map<String, Object> tempUserData = new HashMap<>();
       tempUserData.put("email", email);
       tempUserData.put("prenom", prenom);
       tempUserData.put("nom", nom);
       request.getSession().setAttribute("tempOAuth2User", tempUserData);

       // Redirect to frontend to choose role
       response.sendRedirect("http://localhost:4200/select-role");
   }

}