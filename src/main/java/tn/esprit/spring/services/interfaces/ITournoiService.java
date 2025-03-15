package tn.esprit.spring.services.interfaces;

import tn.esprit.spring.entities.Tournoi;
import java.util.List;

public interface ITournoiService {
    Tournoi createTournoi(Tournoi tournoi);
    List<Tournoi> getAllTournois();
    Tournoi getTournoiById(Integer id);
    Tournoi updateTournoi(Integer id, Tournoi tournoi);
    void deleteTournoi(Integer id);
}
