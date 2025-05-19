package tn.esprit.spring.services.interfaces;

import tn.esprit.spring.entities.Joueur;

import java.util.List;

public interface JoueurInterface {

    // Create a new player
    Joueur addJoueur(Joueur joueur);

    // Get all players
    List<Joueur> getAllJoueurs();

    // Get a player by ID
    Joueur getJoueurById(int id);

    // Update a player
    Joueur updateJoueur(int id, Joueur joueur);

    // Delete a player
    void deleteJoueur(int id);

    // Assign a player to a team
    Joueur assignJoueurToEquipe(int joueurId, int equipeId);

    // Remove a player from a team
    Joueur removeJoueurFromEquipe(int joueurId);

    // Get all players by team
    List<Joueur> getAllPlayerByTeam(int idEquipe);
}