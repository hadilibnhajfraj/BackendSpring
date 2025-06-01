package tn.esprit.spring.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.AuthResponse;
import tn.esprit.spring.entities.LoginRequest;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.entities.UserDTO;
import tn.esprit.spring.repositories.UserRepository;

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


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwt = JwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }


    public UserDTO getUserById(int id) {

        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String display = (u.getPrenom() + " " + u.getNom()).trim();
        if (display.isEmpty()) {
            display = u.getEmail();            // ou u.getUsername()
        }

        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        dto.setDisplayName(display);

        return dto;
    }
}
