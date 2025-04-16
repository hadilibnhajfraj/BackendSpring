package tn.esprit.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.AuthResponse;
import tn.esprit.spring.entities.LoginRequest;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.entities.UserDTO;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.JwtService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwt = JwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}
