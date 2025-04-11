package tn.esprit.spring.services.implementations;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.services.interfaces.PublicationInterface;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicationService implements PublicationInterface {
    PublicationRepository publicationRepository;
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public Publication addPublication(Publication publication, MultipartFile file) throws IOException {
        // Définir le chemin de téléchargement
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Vérifier si le fichier n'est pas null et qu'il n'est pas vide
        if (file != null && !file.isEmpty()) {
            // Définir le chemin absolu pour le fichier
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Sauvegarder le fichier dans le dossier
            Files.write(filePath, file.getBytes());

            // Ajouter le chemin relatif du fichier à l'URL (en utilisant un format d'URL propre)
            String relativeFilePath = "uploads/" + fileName;
            publication.setUrlMedia(relativeFilePath);  // Utilisez un chemin relatif dans l'URL
            publication.setTypeMedia(file.getContentType()); // Enregistrer le type du fichier (image, vidéo, etc.)

            // Afficher un message de journalisation pour vérifier où le fichier est sauvegardé
            System.out.println("Fichier sauvegardé à : " + filePath.toString());
        }

        // Sauvegarder la publication avec ou sans fichier
        return publicationRepository.save(publication);
    }

    @Override
    public List<Publication> retrievePublications() {
        return publicationRepository.findAll();
    }

    @Override
    public Publication updatePublication(Publication publication, MultipartFile file) throws IOException {
        // Définir le chemin du dossier de stockage
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (file != null && !file.isEmpty()) {
            // Générer un nom de fichier unique pour éviter les conflits
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Sauvegarder le fichier dans le dossier
            Files.write(filePath, file.getBytes());

            // Stocker le chemin relatif dans la base de données
            String relativeFilePath = "uploads/" + fileName;
            publication.setUrlMedia(relativeFilePath);
            publication.setTypeMedia(file.getContentType());

            System.out.println("Fichier mis à jour et sauvegardé à : " + filePath.toString());
        }

        return publicationRepository.save(publication);
    }


    @Override
    public Optional<Publication> retrievePublicationById(int idPublication) {
        return publicationRepository.findById(idPublication);  // Utilisation de la méthode findById
    }
    @Override
    public void removePublication(int idPublication) {
        publicationRepository.deleteById(idPublication);
    }

    @Override
    public List<Publication> getPublicationsByUserId(int userId) {
        return publicationRepository.findByUserId(userId);
    }

    private String saveFile(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String filePath = UPLOAD_DIR + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());
        return filePath;
    }
    @Override
    public Publication getLiveMatch() {
        return publicationRepository.findFirstByIsLiveTrueOrderByDatePublicationDesc();
    }

    @Override
    public Publication getLatestVideo() {
        return publicationRepository.findFirstByTypeMediaOrderByDatePublicationDesc("video");
    }

}