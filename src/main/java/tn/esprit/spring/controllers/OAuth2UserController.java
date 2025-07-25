// Nouveau fichier : OAuth2UserController.java
package tn.esprit.spring.controllers; // Ajustez le package si nécessaire

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.User; // Votre entité
import tn.esprit.spring.entities.Role; // Votre énumération Role
import tn.esprit.spring.repositories.UserRepository; // Votre repository
import tn.esprit.spring.services.implementations.JwtService; // Pour générer le token si nécessaire
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth2") // Base URL pour ce contrôleur
@CrossOrigin(origins = "http://localhost:4200") // Autoriser le frontend
public class OAuth2UserController { // Ou PostOAuth2Controller

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService; // Si vous voulez générer un JWT après création

    /**
     * Endpoint pour récupérer les données utilisateur temporaires depuis la session.
     * Accédé par le frontend sur la page /select-role.
     */
    @GetMapping("/temp-user-data")
    public ResponseEntity<?> getTempUserData(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Object> tempUserData = (Map<String, Object>) session.getAttribute("tempOAuth2User");

        if (tempUserData == null) {
            // Cela peut arriver si la session a expiré ou si l'utilisateur accède directement à cette URL
            return ResponseEntity.badRequest().body(Map.of("error", "Aucune donnée utilisateur temporaire trouvée. Veuillez vous reconnecter via OAuth2."));
        }

        // Vérifier si l'utilisateur existe déjà (cas où il reviendrait sur cette page)
        String email = (String) tempUserData.get("email");
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            // L'utilisateur existe déjà, cela ne devrait pas arriver normalement si le flux est bien géré.
            // Nettoyer la session et renvoyer une erreur.
            session.removeAttribute("tempOAuth2User");
            return ResponseEntity.badRequest().body(Map.of("error", "Un utilisateur avec cet email existe déjà. Vous pouvez vous connecter directement."));
        }

        return ResponseEntity.ok(tempUserData);
    }

    /**
     * Endpoint pour finaliser l'inscription de l'utilisateur OAuth2.
     * Appelé par le frontend après sélection du rôle.
     */
    @PostMapping("/finalize-registration")
    public ResponseEntity<?> finalizeRegistration(@RequestBody Map<String, String> payload, HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Object> tempUserData = (Map<String, Object>) session.getAttribute("tempOAuth2User");

        if (tempUserData == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Session invalide ou expirée. Veuillez recommencer la procédure de connexion."));
        }

        String selectedRoleStr = payload.get("role");
        if (selectedRoleStr == null || selectedRoleStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le rôle est obligatoire."));
        }

        Role selectedRole;
        try {
            selectedRole = Role.valueOf(selectedRoleStr); // Convertir String en Enum
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rôle invalide : " + selectedRoleStr));
        }

        String email = (String) tempUserData.get("email");
        // Vérifier à nouveau si l'utilisateur existe (paranoïa)
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            session.removeAttribute("tempOAuth2User");
            return ResponseEntity.badRequest().body(Map.of("error", "Un utilisateur avec cet email existe déjà."));
        }

        // Créer l'utilisateur final
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPrenom((String) tempUserData.getOrDefault("prenom", ""));
        newUser.setNom((String) tempUserData.getOrDefault("nom", ""));
        // Date de naissance: si vous l'obtenez plus tard, vous pouvez la mettre ici.
        // Pour l'instant, la laisser null comme prévu.
        newUser.setDateNaissance((LocalDate) tempUserData.get("dateNaissance")); // Sera null si non fournie
        newUser.setRole(selectedRole);
        // Mot de passe vide pour les utilisateurs OAuth2
        newUser.setPassword(""); // Ou null si votre entité le permet

        try {
            User savedUser = userRepository.save(newUser);
            System.out.println("Nouvel utilisateur OAuth2 enregistré avec succès : " + savedUser.getEmail() + " (Rôle: " + savedUser.getRole() + ")");

            // Facultatif: Générer un JWT pour l'utilisateur nouvellement créé
            // Assurez-vous que JwtService.generateToken est static OU injectez JwtService
            String token = JwtService.generateToken(savedUser);

            // Nettoyer la session
            session.removeAttribute("tempOAuth2User");

            // Retourner le token et/ou les détails de l'utilisateur
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inscription réussie !");
            response.put("token", token);
            response.put("user", Map.of( // Envoyer des détails simples
                    "id", savedUser.getId(),
                    "email", savedUser.getEmail(),
                    "prenom", savedUser.getPrenom(),
                    "nom", savedUser.getNom(),
                    "role", savedUser.getRole().name()
            ));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'utilisateur OAuth2 : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Erreur interne lors de l'enregistrement."));
        }
    }
}