package tn.esprit.spring.services.interfaces;

import tn.esprit.spring.entities.User;

import java.util.List;

public interface JoueurInterface {

    // Créer un nouveau joueur
    User addJoueur(User joueur);

    // Récupérer tous les joueurs
    List<User> getAllJoueurs();

    // Récupérer un joueur par ID
    User getJoueurById(int id);

    // Mettre à jour un joueur
    User updateJoueur(int id, User joueur);

    // Supprimer un joueur
    void deleteJoueur(int id);

    // Associer un joueur à une équipe
    User assignJoueurToEquipe(int joueurId, int equipeId);

    // Retirer un joueur d'une équipe
    User removeJoueurFromEquipe(int joueurId);

}
