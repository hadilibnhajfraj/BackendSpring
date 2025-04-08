package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.Publication;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.JwtService;
import tn.esprit.spring.services.interfaces.PublicationInterface;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/publications")
@AllArgsConstructor
public class PublicationController {
    PublicationInterface publicationService;
    private final JwtService jwtService;
    UserRepository userRepository;
/*
    @PostMapping("/add")
    public ResponseEntity<Publication> addPublication(@RequestPart("publication") Publication publication,
                                                      @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(publicationService.addPublication(publication, file));
    }
*/
@PostMapping("/add")
public ResponseEntity<Publication> addPublication(
        @RequestPart("publication") String publicationJson,
        @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

    System.out.println("Authenticated user role: " + jwtService.getAuthenticatedUserRole());

    if (!"Presse".equals(jwtService.getAuthenticatedUserRole())) {
        return ResponseEntity.status(403).body(null);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    Publication publication = objectMapper.readValue(publicationJson, Publication.class);

    // 🔐 Associer l'utilisateur connecté
    String email = jwtService.getEmailFromAuthenticatedUser(); // 👈 à ajouter dans JwtService

    userRepository.findByEmail(email).ifPresent(publication::setUser);

    return ResponseEntity.ok(publicationService.addPublication(publication, file));
}


    @GetMapping("/all")
    public List<Publication> getAllPublications() {
        return publicationService.retrievePublications();
    }

    @PutMapping(value = "/updatePublication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Publication> updatePublication(
            @RequestPart("publication") String publicationJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // Convertir publicationJson en objet Publication
        ObjectMapper objectMapper = new ObjectMapper();
        Publication publication = objectMapper.readValue(publicationJson, Publication.class);

        return ResponseEntity.ok(publicationService.updatePublication(publication, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublication(@PathVariable int id) {
        return ResponseEntity.ok(publicationService.retrievePublication(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable int id) {
        publicationService.removePublication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<Publication> getPublicationsByUser(@PathVariable Long userId) {
        return publicationService.getPublicationsByUserId(userId);
    }
}