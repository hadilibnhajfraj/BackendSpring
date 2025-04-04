package tn.esprit.spring.services.implimentations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.interfaces.ITournoiService;




import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournoiService implements ITournoiService {

    private final TournoiRepository tournoiRepository;
    private final EquipeRepository equipeRepository;
    private final TournoiEquipeRepository tournoiEquipeRepository;
    private final MatchFoRepository matchFoRepository;
    private final TerrainRepository terrainRepository;
    private final PlanningRepository planningRepository;


    @Override
    @Transactional
    public Tournoi createTournoi(Tournoi tournoi) {
        return tournoiRepository.save(tournoi);
    }

    @Override
    public List<Tournoi> getAllTournois() {
        List<Tournoi> tournois = tournoiRepository.findAll();

        // Ajouter la propriété hasMatchs pour chaque tournoi
        for (Tournoi tournoi : tournois) {
            tournoi.setHasMatchs(tournoiADejaDesMatchs(tournoi.getIdTournoi()));
        }

        return tournois;
    }

    public Tournoi getTournoiById(Integer id) {
        return tournoiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournoi non trouvé"));

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

    @Override
    @Transactional
    public Tournoi affecterEquipesATournoi(Integer tournoiId, List<Integer> equipeIds) {
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        List<Equipe> equipes = equipeRepository.findAllById(equipeIds);

        // Vérifier si on peut ajouter ces équipes
        if (tournoi.getNbEquipeRestant() < equipes.size()) {
            throw new IllegalStateException("Impossible d'ajouter ces équipes : dépassement du nombre maximum.");
        }

        for (Equipe equipe : equipes) {
            // Vérifier si l'équipe est déjà affectée
            boolean equipeDejaAffectee = tournoi.getTournoiEquipes().stream()
                    .anyMatch(te -> te.getEquipe().getIdEquipe() == equipe.getIdEquipe());

            if (equipeDejaAffectee) {
                throw new IllegalStateException("L'équipe " + equipe.getNom() + " est déjà inscrite dans ce tournoi.");
            }

            // Ajouter l'équipe au tournoi
            TournoiEquipe tournoiEquipe = new TournoiEquipe();
            tournoiEquipe.setTournoi(tournoi);
            tournoiEquipe.setEquipe(equipe);
            tournoiEquipeRepository.save(tournoiEquipe);

            // Mettre à jour le nombre d'équipes restantes
            tournoi.setNbEquipeRestant(tournoi.getNbEquipeRestant() - 1);
        }

        // Sauvegarde du tournoi mis à jour
        tournoiRepository.save(tournoi);
        return tournoi;
    }
    @Override
    public void desaffecterEquipeDuTournoi(Integer tournoiId, Integer equipeId) {
        // Trouver le tournoi par son ID
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        // Trouver l'équipe par son ID
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new NoSuchElementException("Equipe introuvable"));

        // Trouver l'association entre le tournoi et l'équipe dans la table de jointure TournoiEquipe
        TournoiEquipe tournoiEquipe = tournoiEquipeRepository.findByTournoiAndEquipe(tournoi, equipe)
                .orElseThrow(() -> new NoSuchElementException("L'équipe n'est pas associée à ce tournoi"));

        // Supprimer cette association
        tournoiEquipeRepository.delete(tournoiEquipe);

        // Mettre à jour le nombre d'équipes restantes dans le tournoi
        tournoi.setNbEquipeRestant(tournoi.getNbEquipeRestant() + 1);
        tournoiRepository.save(tournoi);
    }


    @Transactional
    public List<Equipe> getEquipesNonInscrites(Integer tournoiId) {
        // Trouver le tournoi
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        // Récupérer les équipes inscrites au tournoi
        List<Equipe> equipesInscrites = tournoi.getTournoiEquipes().stream()
                .map(te -> te.getEquipe())
                .collect(Collectors.toList());

        // Récupérer toutes les équipes qui ne sont pas encore inscrites au tournoi
        List<Equipe> equipesNonInscrites = equipeRepository.findAll().stream()
                .filter(equipe -> !equipesInscrites.contains(equipe))
                .collect(Collectors.toList());

        return equipesNonInscrites;
    }


    @Override
    public List<Equipe> getEquipesParTournoi(Integer tournoiId) {
        // Trouver le tournoi par son ID
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        // Récupérer les équipes associées à ce tournoi
        // Cela suppose qu'il existe une relation entre tournoi et équipe, par exemple via une table de jointure (TournoiEquipe)
        return tournoiEquipeRepository.findAllByTournoi(tournoi).stream()
                .map(TournoiEquipe::getEquipe) // Récupérer l'équipe depuis l'association TournoiEquipe
                .collect(Collectors.toList());
    }

    public boolean tournoiADejaDesMatchs(Integer idTournoi) {
        return matchFoRepository.existsByTournoi_IdTournoi(idTournoi);
    }

    public List<MatchFo> getMatchsParTournoi(Integer tournoiId) {
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        return matchFoRepository.findByTournoi(tournoi);
    }


    @Transactional
    public String genererPremierTour(Integer idTournoi) {
        // Vérifier si des matchs existent déjà pour ce tournoi
        if (tournoiADejaDesMatchs(idTournoi)) {
            throw new IllegalStateException("Un tirage au sort a déjà été effectué pour ce tournoi.");
        }

        Tournoi tournoi = tournoiRepository.findById(idTournoi)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        List<Equipe> equipes = getEquipesParTournoi(idTournoi);
        if (equipes.size() < 2) {
            throw new IllegalStateException("Il faut au moins 2 équipes pour organiser un tournoi.");
        }

        // Mélange aléatoire des équipes
        Collections.shuffle(equipes);

        List<MatchFo> matchs = new ArrayList<>();
        StringBuilder result = new StringBuilder("Tour 1 :\n");

        // Création des matchs par paires successives
        for (int i = 0; i < equipes.size() - 1; i += 2) {
            MatchFo match = new MatchFo();
            match.setNom("Match " + (i / 2 + 1) + " - Tour 1");
            match.setTournoi(tournoi);
            match.setTour(1);
            match.setDateMatch(LocalDate.now());
            match.setHeureMatch(Time.valueOf(LocalTime.of(15, 0)));
            match.setEquipes1(List.of(equipes.get(i), equipes.get(i + 1)));

            matchs.add(match);
            result.append(match.getNom()).append(" : ")
                    .append(equipes.get(i).getNom()).append(" vs ")
                    .append(equipes.get(i + 1).getNom()).append("\n");
        }

        matchFoRepository.saveAll(matchs);

        return result.toString();
    }

    @Transactional
    @Override
    public String mettreAJourScores(Integer matchId, int scoreEquipe1, int scoreEquipe2) {
        MatchFo match = matchFoRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match introuvable"));

        match.setScoreEquipe1(scoreEquipe1);
        match.setScoreEquipe2(scoreEquipe2);

        Equipe equipeGagnante = scoreEquipe1 > scoreEquipe2 ? match.getEquipes1().get(0) : match.getEquipes1().get(1);

        matchFoRepository.save(match);

        return "Scores mis à jour avec succès ! Équipe gagnante : " + equipeGagnante.getNom();
    }


    @Transactional
    public String genererTourSuivant(Integer tournoiId) {
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        int dernierTour = matchFoRepository.findMaxTourParTournoi(tournoiId);
        List<MatchFo> matchsPrecedents = matchFoRepository.findByTournoiAndTour(tournoi, dernierTour);

        // Récupérer les équipes gagnantes SANS doublons
        Set<Equipe> equipesGagnantes = new LinkedHashSet<>(getEquipesGagnantes(matchsPrecedents));

        if (equipesGagnantes.size() < 2) {
            return "Tournoi terminé ! Équipe gagnante : " + equipesGagnantes.iterator().next().getNom();
        }

        List<MatchFo> nouveauxMatchs = new ArrayList<>();
        StringBuilder result = new StringBuilder("Tour " + (dernierTour + 1) + " :\n");

        List<Equipe> listeEquipes = new ArrayList<>(equipesGagnantes);

        // Génération des matchs pour le tour suivant
        for (int i = 0; i < listeEquipes.size() - 1; i += 2) {
            MatchFo match = new MatchFo();
            match.setNom("Match " + (i / 2 + 1) + " - Tour " + (dernierTour + 1));
            match.setTournoi(tournoi);
            match.setTour(dernierTour + 1);
            match.setDateMatch(LocalDate.now().plusDays(dernierTour));
            match.setHeureMatch(Time.valueOf(LocalTime.of(15, 0)));
            match.setEquipes1(List.of(listeEquipes.get(i), listeEquipes.get(i + 1)));

            nouveauxMatchs.add(match);
            result.append(match.getNom()).append(" : ")
                    .append(listeEquipes.get(i).getNom()).append(" vs ")
                    .append(listeEquipes.get(i + 1).getNom()).append("\n");
        }

        matchFoRepository.saveAll(nouveauxMatchs);
        return result.toString();
    }


    private List<Equipe> getEquipesGagnantes(List<MatchFo> matchs) {
        List<Equipe> gagnants = new ArrayList<>();
        for (MatchFo match : matchs) {
            if (match.getScoreEquipe1() > match.getScoreEquipe2()) {
                gagnants.add(match.getEquipes1().get(0));
            } else {
                gagnants.add(match.getEquipes1().get(1));
            }
        }
        return gagnants;
    }
    // Méthode pour récupérer les terrains disponibles pour un tournoi à une date et heure spécifiques
  /*  public List<Terrain> getTerrainsDisponiblesPourTournoi(Integer tournoiId, LocalDate dateMatch, LocalTime heureMatch) {
        // Récupérer le tournoi
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        // Récupérer tous les matchs associés au tournoi
        List<MatchFo> matchs = matchFoRepository.findByTournoi(tournoi);

        // Récupérer tous les terrains
        List<Terrain> terrains = terrainRepository.findAll();

        // Filtrer les terrains disponibles (non réservés pour la même date et heure)
        List<Terrain> terrainsDisponibles = terrains.stream()
                .filter(terrain ->
                        matchs.stream()
                                .noneMatch(match -> match.getTerrain().equals(terrain) &&
                                        match.getDateMatch().equals(dateMatch) &&
                                        match.getHeureMatch().equals(Time.valueOf(heureMatch)))
                )
                .collect(Collectors.toList());

        return terrainsDisponibles;
    }
*/
    public void affecterTerrainAMatch(Integer matchId, Integer terrainId) {
        // Récupérer le match et le terrain par leurs identifiants
        MatchFo match = matchFoRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match introuvable"));

        Terrain terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new NoSuchElementException("Terrain introuvable"));

        // Vérifier si le terrain est déjà réservé pour la date et l'heure du match
        if (matchFoRepository.existsByTerrainAndDateMatchAndHeureMatch(terrain, match.getDateMatch(), match.getHeureMatch())) {
            throw new IllegalStateException("Ce terrain est déjà réservé pour cette date et heure.");
        }

        // Affecter le terrain au match
        match.setTerrain(terrain);
        matchFoRepository.save(match);
    }



}