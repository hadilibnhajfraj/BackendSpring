package tn.esprit.spring.services.implementations;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.repositories.JoueurRepository;
import tn.esprit.spring.services.interfaces.JoueurInterface;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoueurService implements JoueurInterface {

    private final JoueurRepository joueurRepository;
    private final EquipeRepository equipeRepository;
    @Override
    public User addJoueur(User joueur) {

        // Persister le joueur dans la base de données
        return joueurRepository.save(joueur);
    }

    @Override
    public List<User> getAllJoueurs() {
        // Récupérer tous les joueurs
        return joueurRepository.findAll();
    }

    @Override
    public User getJoueurById(int id) {

        // Récupérer un joueur par ID
        Optional<User> joueur = joueurRepository.findById(id);
        return joueur.orElse(null); // Retourne null si le joueur n'est pas trouvé
    }

    @Override
    public User updateJoueur(int id, User joueur) {
        // Vérifier si le joueur existe
        Optional<User> existingJoueur = joueurRepository.findById(id);
        if (existingJoueur.isPresent()) {
            User joueurToUpdate = existingJoueur.get();
            joueurToUpdate.setNom(joueur.getNom());
            joueurToUpdate.setPrenom(joueur.getPrenom());
            joueurToUpdate.setDateNaissance(joueur.getDateNaissance());
            // Mettre à jour d'autres champs si nécessaire
            return joueurRepository.save(joueurToUpdate);
        }
        return null; // Retourne null si le joueur n'existe pas
    }


    @Override
    public void deleteJoueur(int id) {
        // Supprimer un joueur par ID
        joueurRepository.deleteById(id);
    }

    @Override
    public User assignJoueurToEquipe(int joueurId, int equipeId) {
        // Récupérer le joueur et l'équipe
        Optional<User> joueurOptional = joueurRepository.findById(joueurId);
        Optional<Equipe> equipeOptional = equipeRepository.findById(equipeId);

        if (joueurOptional.isPresent() && equipeOptional.isPresent()) {
            User joueur = joueurOptional.get();
            Equipe equipe = equipeOptional.get();

            // Associer le joueur à l'équipe
            joueur.setEquipe(equipe);

            // Mettre à jour le joueur dans la base de données
            return joueurRepository.save(joueur);
        }

        return null; // Retourne null si l'un des éléments n'existe pas
    }


    @Override
    public User removeJoueurFromEquipe(int joueurId) {
        // Récupérer le joueur
        Optional<User> joueurOptional = joueurRepository.findById(joueurId);

        if (joueurOptional.isPresent()) {
            User joueur = joueurOptional.get();

            // Retirer l'équipe associée
            joueur.setEquipe(null);

            // Mettre à jour le joueur dans la base de données
            return joueurRepository.save(joueur);
        }

        return null; // Retourne null si le joueur n'existe pas
    }
}