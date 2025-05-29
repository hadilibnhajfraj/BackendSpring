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
            equipe.setIdEquipe(id);  // Maintenir l'ID existant lors de la mise à jour
            return equipeRepository.save(equipe);  // Sauvegarder l'équipe mise à jour
        }
        return null;  // Retourner null si l'équipe n'existe pas
    }

    @Override
    public void deleteEquipe(Integer id) {

        if (equipeRepository.existsById(id)) {
            equipeRepository.deleteById(id);  // Supprimer l'équipe si elle existe
        }
    }
    /// todo: send message or email to infrom the player that he is added to the team
    @Override
    public Equipe ajouterJoueur(Integer idEquipe, Integer idJoueur) throws MessagingException {
        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Equipe not found"));

        Joueur joueur =     joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new EntityNotFoundException("Joueur not found"));

        // Update both sides of the relationship
        joueur.setEquipe(equipe);

        // Update associated user if exists
        if (joueur.getUser() != null) {
            joueur.getUser().setEquipe(equipe);
            emailService.sendEmail(
                    joueur.getMail(),
                    "You have been added to a team",
                    "Hello " + joueur.getUser().getPrenom() + ",\n\n" +
                            "You have been successfully added to the team: " + equipe.getNom() + ".\n\n" +
                            "Best regards,\n" +
                            "The Team Management"
            );
            userRepository.save(joueur.getUser());
        }

        // Increment player count
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

             // Remove the player from the team
             if (equipe.getUsers().remove(joueur)) { // Remove the player from the team's list
                 joueur.setEquipe(null); // Disassociate the team from the player

                 // Save the changes
joueurRepository.save(joueurOptional.get()); // Save the player
                 equipeRepository.save(equipe); // Save the team                 equipeRepository.save(equipe); // Save the team

                 return equipe; // Return the updated team
             }
         }
     }

     return null; // Return null if the team or player does not exist
 }
}
