package tn.esprit.spring.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.AuthResponse;
import tn.esprit.spring.entities.LoginRequest;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.entities.UserDTO;
import tn.esprit.spring.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

   /* public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setNom(userDTO.nom);
        user.setPrenom(userDTO.prenom);
        user.setEmail(userDTO.email);
        user.setPassword(passwordEncoder.encode(userDTO.password)); // encode
        user.setRole(userDTO.role);
        String jwt = JwtService.generateToken(user);
        return userRepository.save(user);
    }*/
   public AuthResponse register(UserDTO userDTO) {
       User user = new User();
       user.setNom(userDTO.nom);
       user.setPrenom(userDTO.prenom);
       user.setEmail(userDTO.email);
       user.setPassword(passwordEncoder.encode(userDTO.password));
       user.setRole(userDTO.role);

       userRepository.save(user);

       String jwt = JwtService.generateToken(user);
       return new AuthResponse(jwt);
   }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(int id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setNom(userDetails.getNom());
                    user.setPrenom(userDetails.getPrenom());
                    user.setDateNaissance(userDetails.getDateNaissance());
                    user.setRole(userDetails.getRole());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }



    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("Email reçu : " + request.getEmail());
            System.out.println("Mot de passe reçu : " + request.getPassword());
            System.out.println("Utilisateur trouvé : " + user);
            throw new RuntimeException("Invalid credentials");
        }

        String jwt = JwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}
