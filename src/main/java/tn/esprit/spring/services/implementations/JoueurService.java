package tn.esprit.spring.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.Joueur;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.repositories.JoueurRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.interfaces.JoueurInterface;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JoueurService implements JoueurInterface {

    private final JoueurRepository joueurRepository;
    private final UserRepository userRepository;
    private final EquipeRepository equipeRepository;

    @Override
    public Joueur addJoueur(Joueur joueur) {
        // If User is provided but not persisted yet (no ID)
        if (joueur.getUser() != null && joueur.getUser().getId() == 0) {
            // Create new User from Joueur data
            User newUser = new User();
            newUser.setNom(joueur.getNom());
            newUser.setPrenom(joueur.getPrenom());
            newUser.setRole(Role.Joueur);

            // Save the User first
            User savedUser = userRepository.save(newUser);
            joueur.setUser(savedUser);
        }
        // If User ID is provided, verify it exists
        else if (joueur.getUser() != null) {
            User existingUser = userRepository.findById(joueur.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            joueur.setUser(existingUser);
        }

        return joueurRepository.save(joueur);
    }

    @Override
    public List<Joueur> getAllJoueurs() {
        return joueurRepository.findAll();
    }

    @Override
    public Joueur getJoueurById(int id) {
        return joueurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur not found with id: " + id));
    }

    @Override
    public Joueur updateJoueur(int id, Joueur joueur) {
        Joueur existingJoueur = joueurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur not found"));

        existingJoueur.setNom(joueur.getNom());
        existingJoueur.setPrenom(joueur.getPrenom());
        existingJoueur.setTaille(joueur.getTaille());
        existingJoueur.setPoids(joueur.getPoids());
        existingJoueur.setPiedFort(joueur.getPiedFort());
        existingJoueur.setPoste(joueur.getPoste());
        existingJoueur.setMail(joueur.getMail());
        existingJoueur.setTel(joueur.getTel());
        existingJoueur.setDescription(joueur.getDescription());

        if (joueur.getUser() != null) {
            User user = userRepository.findById(joueur.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingJoueur.setUser(user);
        }

        return joueurRepository.save(existingJoueur);
    }

    @Override
    public void deleteJoueur(int id) {
        Joueur joueur = joueurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur not found"));

        if (joueur.getUser() != null) {
            joueur.getUser().setJoueur(null);
        }

        joueurRepository.delete(joueur);
    }

    @Override
    public Joueur assignJoueurToEquipe(int joueurId, int equipeId) {
        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new RuntimeException("Joueur not found"));
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        joueur.setEquipe(equipe);
        return joueurRepository.save(joueur);
    }

    @Override
    public Joueur removeJoueurFromEquipe(int joueurId) {
        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new RuntimeException("Joueur not found"));

        joueur.setEquipe(null);
        return joueurRepository.save(joueur);
    }

    @Override
    public List<Joueur> getAllPlayerByTeam(int idEquipe) {
        // Verify team exists first
        if (!equipeRepository.existsById(idEquipe)) {
            throw new EntityNotFoundException("Equipe not found with id: " + idEquipe);
        }

        // Use custom query to avoid loading entire Equipe entity
        return joueurRepository.findByEquipeId(idEquipe);
    }

    @Override
    public List<Joueur> findAllNonAssignedJoueurs() {
        return joueurRepository.findAllNonAssignedJoueurs();
    }

    @Override
    public List<Joueur> getAllNonAssignedJoueurs() {
        return joueurRepository.findAllNonAssignedJoueurs();
    }
}