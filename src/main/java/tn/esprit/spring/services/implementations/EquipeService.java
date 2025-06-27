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
    private final JoueurRepository joueurRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public Equipe ajouterEquipe(Equipe equipe) {
        return equipeRepository.save(equipe);
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
            equipe.setIdEquipe(id);
            return equipeRepository.save(equipe);
        }
        throw new EntityNotFoundException("Equipe non trouvée avec l'ID : " + id);
    }

    @Override
    public void deleteEquipe(Integer id) {
        if (!equipeRepository.existsById(id)) {
            throw new EntityNotFoundException("Equipe non trouvée avec l'ID : " + id);
        }
        equipeRepository.deleteById(id);
    }

    @Override
    public Equipe ajouterJoueur(Integer idEquipe, Integer idJoueur) throws MessagingException {
        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Equipe non trouvée avec l'ID : " + idEquipe));

        Joueur joueur = joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé avec l'ID : " + idJoueur));

        joueur.setEquipe(equipe);

        if (joueur.getUser() != null) {
            joueur.getUser().setEquipe(equipe);
            emailService.sendEmail(
                    joueur.getMail(),
                    "Vous avez été ajouté à une équipe",
                    "Salut " + joueur.getUser().getPrenom() + ",\n\n" +
                            "Vous avez été ajouté avec succès à l'équipe : " + equipe.getNom() + ".\n\n" +
                            "Cordialement,\n" +
                            "Meriem Ben Salem"
            );
            userRepository.save(joueur.getUser());
        }

        equipe.setNb_joueur(equipe.getNb_joueur() + 1);

        joueurRepository.save(joueur);
        return equipeRepository.save(equipe);
    }

    @Override
    public Equipe retirerJoueur(Integer idEquipe, Integer idJoueur) {
        Equipe equipe = equipeRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Equipe non trouvée avec l'ID : " + idEquipe));

        Joueur joueur = joueurRepository.findById(idJoueur)
                .orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé avec l'ID : " + idJoueur));

        if (!equipe.equals(joueur.getEquipe())) {
            throw new IllegalStateException("Le joueur n'appartient pas à cette équipe.");
        }

        // Désassociation
        joueur.setEquipe(null);
        if (joueur.getUser() != null) {
            joueur.getUser().setEquipe(null);
            userRepository.save(joueur.getUser());
        }

        equipe.setNb_joueur(equipe.getNb_joueur() - 1);

        joueurRepository.save(joueur);
        return equipeRepository.save(equipe);
    }
}
