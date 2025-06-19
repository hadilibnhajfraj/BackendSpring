package tn.esprit.spring.services.implementations;


import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.Joueur;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.repositories.JoueurRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.interfaces.EquipeInterface;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EquipeService implements EquipeInterface {


    private final EquipeRepository equipeRepository;
    private  final JoueurRepository joueurRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public Equipe ajouterEquipe(Equipe equipe) {
        return  equipeRepository.save(equipe);
    }

    @Override
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    @Override
    public Optional<Equipe> getEquipeById(Integer id) {
        return equipeRepository.findById(id);
    }

    @Override
    public Equipe updateEquipe(Integer id, Equipe equipe) {
        if (equipeRepository.existsById(id)) {
            // Maintenir l'ID existant lors de la mise à jour
            equipe.setIdEquipe(id);
            // Sauvegarder l'équipe mise à jour
            return equipeRepository.save(equipe);
        }
        // Retourner null si l'équipe n'existe pas
        return null;
    }

    @Override
    public void deleteEquipe(Integer id) {

        if (equipeRepository.existsById(id)) {
            // Supprimer l'équipe si elle existe
            equipeRepository.deleteById(id);
        }
    }
    //***************Email
    /// envoyer un message ou un email pour informer le joueur qu'il a été ajouté à l'équipe
    @Override
    public Equipe ajouterJoueur(Integer idEquipe, Integer idJoueur) throws MessagingException {
        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Equipe not found"));

        Joueur joueur =     joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new EntityNotFoundException("Joueur not found"));

        // Mettre à jour les deux côtés de la relation
        joueur.setEquipe(equipe);

        // Mettre à jour l'utilisateur associé s'il existe
        if (joueur.getUser() != null) {
            joueur.getUser().setEquipe(equipe);
            emailService.sendEmail(
                    joueur.getMail(),
                    "vous avez été ajouté à une équipe",
                    "Salut " + joueur.getUser().getPrenom() + ",\n\n" +
                            "Vous avez été ajouté avec succès à l'équipe : " + equipe.getNom() + ".\n\n" +
                            "Cordialement,\n" +
                            "Meriem Ben Salem"
            );
            userRepository.save(joueur.getUser());
        }

        // Incrémenter le nombre de joueurs
        equipe.setNb_joueur(equipe.getNb_joueur() + 1);

        // Save changes
        joueurRepository.save(joueur);
        return equipeRepository.save(equipe);
    }


    @Override
 public Equipe retirerJoueur(Integer id, Integer playerId) {
     Optional<Equipe> equipeOptional = equipeRepository.findById(id); // Retrieve the team
     if (equipeOptional.isPresent()) {
         Equipe equipe = equipeOptional.get();

         Optional<Joueur> joueurOptional = joueurRepository.findById(playerId); // Retrieve the player
         if (joueurOptional.isPresent()) {
             User joueur = joueurOptional.get().getUser();

             // Retirer le joueur de l'équipe
             if (equipe.getUsers().remove(joueur)) { // Remove the player from the team's list
                 // Désassocier l'équipe du joueur
                 joueur.setEquipe(null);

                 // Save the changes
joueurRepository.save(joueurOptional.get()); // Save the player
                 // Save the team
                 equipeRepository.save(equipe);
             // Retourner l'équipe mise à jour
                 return equipe;
             }
         }
     }

     return null; // Return null if the team or player does not exist
 }
}
