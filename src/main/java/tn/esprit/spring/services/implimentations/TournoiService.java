package tn.esprit.spring.services.implimentations;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Tournoi;
import tn.esprit.spring.repositories.TournoiRepository;
import tn.esprit.spring.services.interfaces.ITournoiService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournoiService implements ITournoiService {
    private final TournoiRepository tournoiRepository;

    @Override
    public Tournoi createTournoi(Tournoi tournoi) {
        return tournoiRepository.save(tournoi);
    }

    @Override
    public List<Tournoi> getAllTournois() {
        return tournoiRepository.findAll();
    }

    @Override
    public Tournoi getTournoiById(Integer id) {
        return tournoiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournoi non trouvé avec ID : " + id));
    }

    @Override
    public Tournoi updateTournoi(Integer id, Tournoi tournoiDetails) {
        Tournoi tournoi = getTournoiById(id);
        tournoi.setNom(tournoiDetails.getNom());
        tournoi.setNbEquipe(tournoiDetails.getNbEquipe());
        tournoi.setFrais(tournoiDetails.getFrais());
        tournoi.setDateDebut(tournoiDetails.getDateDebut());
        tournoi.setDateFin(tournoiDetails.getDateFin());
        return tournoiRepository.save(tournoi);
    }

    @Override
    public void deleteTournoi(Integer id) {
        tournoiRepository.deleteById(id);
    }
}

