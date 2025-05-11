package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.Publication;

import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.JwtService;
import tn.esprit.spring.services.interfaces.PublicationInterface;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/publications")
@AllArgsConstructor
public class PublicationController {

    private final PublicationInterface publicationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @PostMapping("/add")
    public ResponseEntity<Publication> addPublication(
            @RequestPart("publication") String publicationJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        if (!"Presse".equals(jwtService.getAuthenticatedUserRole())) {
            return ResponseEntity.status(403).body(null);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Publication publication = objectMapper.readValue(publicationJson, Publication.class);

        String email = jwtService.getEmailFromAuthenticatedUser();
        userRepository.findByEmail(email).ifPresent(publication::setUser);

        // Retourner la publication créée
        Publication createdPublication = publicationService.addPublication(publication, file);
        return ResponseEntity.ok(createdPublication);
    }



 @PutMapping(value = "/updatePublication/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<Publication> updatePublication(
         @PathVariable("id") int id,
         @RequestPart("publication") String publicationJson,
         @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

     if (!"Presse".equals(jwtService.getAuthenticatedUserRole())) {
         return ResponseEntity.status(403).body(null);
     }

     ObjectMapper objectMapper = new ObjectMapper();
     Publication publication = objectMapper.readValue(publicationJson, Publication.class);
     publication.setId(id); // Assigner l'id de la publication à modifier

     // 🔥 Récupération de l'utilisateur à partir du token
     String email = jwtService.getEmailFromAuthenticatedUser();
     Optional<User> userOptional = userRepository.findByEmail(email);
     userOptional.ifPresent(publication::setUser); // Assigner l'utilisateur à la publication

     return ResponseEntity.ok(publicationService.updatePublication(publication, file));
 }


    @GetMapping("/getPublication/{id}")
    public ResponseEntity<Publication> getPublication(@PathVariable int id) {
        Optional<Publication> publication = publicationService.retrievePublicationById(id);

        if (publication.isPresent()) {
            return ResponseEntity.ok(publication.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable int id) {
        if (!"Presse".equals(jwtService.getAuthenticatedUserRole())) {
            return ResponseEntity.status(403).build();
        }

        publicationService.removePublication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public List<Publication> getAllPublications() {
        return publicationService.retrievePublications();
    }

    /*@GetMapping("/user/{userId}")
    public List<Publication> getPublicationsByUser(@PathVariable Long userId) {
        return publicationService.getPublicationsByUserId(userId);
    }*/
    @PreAuthorize("hasRole('Presse')")
    @GetMapping("/mine")
    @Transactional
    public ResponseEntity<List<Publication>> getMyPublications(@RequestHeader("Authorization") String authorizationHeader) {
        // Vérifier si l'en-tête Authorization est présent et commence par "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Token manquant ou invalide.");
            return ResponseEntity.badRequest().body(null);  // Retourner 400 Bad Request en cas de token manquant ou invalide
        }

        // Extraire le token à partir de l'en-tête Authorization
        String token = authorizationHeader.substring(7);

        // Extraire l'email de l'utilisateur authentifié à partir du token
        String email = jwtService.getEmailFromAuthenticatedUser();
        System.out.println("Email extrait du token : " + email);

        // Vérifier si l'utilisateur existe dans la base de données
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Utilisateur trouvé : " + user.getEmail() + ", Role : " + user.getRole());

            // Comparaison des rôles, pour vérifier si l'utilisateur a le rôle 'Presse'
            if (Role.Presse.equals(user.getRole())) {
                System.out.println("L'utilisateur a le rôle Presse.");
                List<Publication> publications = user.getPublications();
                System.out.println("Mes publications : " + publications);
                return publications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publications);


            } else {
                System.out.println("L'utilisateur n'a pas le rôle Presse. Rôle actuel : " + user.getRole());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden
            }
        } else {
            // Si l'utilisateur n'existe pas dans la base de données
            System.out.println("Utilisateur non trouvé avec l'email : " + email);
            return ResponseEntity.notFound().build();  // 404 Not Found si l'utilisateur n'est pas trouvé
        }
    }
    @PostMapping("/start-live")
    public ResponseEntity<Publication> startLive() {
        // Vérifie le rôle
        if (!"Presse".equals(jwtService.getAuthenticatedUserRole())) {
            return ResponseEntity.status(403).body(null);
        }

        String email = jwtService.getEmailFromAuthenticatedUser();
        User user = userRepository.findByEmail(email).orElseThrow();

        Publication pub = new Publication();
        pub.setUser(user);
        pub.setContenu("Live en cours");
        pub.setIsLive(true);
        pub.setDatePublication(LocalDate.now());

        publicationRepository.save(pub);
        System.out.println("Live démarré avec ID: " + pub.getId());
        return ResponseEntity.ok(pub);
    }


}
