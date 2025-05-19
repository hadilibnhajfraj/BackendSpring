package tn.esprit.spring.services.interfaces;

import tn.esprit.spring.entities.Equipe;
import java.util.List;
import java.util.Optional;
public interface EquipeInterface {

    // Créer une nouvelle équipe
    Equipe ajouterEquipe(Equipe equipe);

    // Récupérer toutes les équipes
    List<Equipe> getAllEquipes();

    // Récupérer une équipe par ID
    Optional<Equipe> getEquipeById(Integer id);

    // Mettre à jour une équipe
    Equipe updateEquipe(Integer id, Equipe equipe);

    // Supprimer une équipe
    void deleteEquipe(Integer id);

    // Ajouter un joueur à une équipe
    Equipe ajouterJoueur(Integer id, Integer playerId);

    // Retirer un joueur d'une équipe
    Equipe retirerJoueur(Integer id, Integer playerId);
}
