package tn.esprit.spring.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.entities.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public String createResetToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            String token = UUID.randomUUID().toString();
            // Ici tu peux enregistrer le token en base de données si besoin
            // Ex: user.setResetToken(token); userRepository.save(user);
            return token;
        }
        return null;
    }
    public void saveResetToken(String email, String token) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setResetToken(token); // assure-toi que le champ existe dans l'entité User
            userRepository.save(user);
        }
    }
    public String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

}