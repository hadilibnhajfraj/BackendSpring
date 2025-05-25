package tn.esprit.spring.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.AuthResponse;
import tn.esprit.spring.entities.LoginRequest;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.entities.UserDTO;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.UserService;
import tn.esprit.spring.services.implementations.EmailService;
import tn.esprit.spring.services.implementations.PasswordResetService;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;
    /* @PostMapping("/register")
     public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
         User user = userService.createUser(userDTO);
         return ResponseEntity.ok(user);
     }*/
    private String generateTempPassword() {
        // Exemple simple : chaîne de 12 caractères alphanumériques
        int length = 12;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDTO userDTO) {
        AuthResponse response = userService.register(userDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email introuvable");
        }

        User user = userOpt.get();

        // Génère le mot de passe temporaire et l'envoie par email
        String tempPassword = emailService.sendTemporaryPassword(email);

        // Encode et met à jour le mot de passe dans la base
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(tempPassword));
        userRepository.save(user);

        // Optionnel : si tu préfères séparer la génération et l'envoi, tu peux utiliser sendPasswordResetEmail
        // emailService.sendPasswordResetEmail(email, tempPassword);

        // Retourne le mot de passe temporaire dans la réponse JSON
        Map<String, String> response = new HashMap<>();
        response.put("tempPassword", tempPassword);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String tempPassword = request.get("tempPassword");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Email introuvable"));
        }

        User user = userOpt.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(tempPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Mot de passe temporaire invalide"));
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Mot de passe mis à jour avec succès"));

    }



}
