package tn.esprit.spring.services.implimentations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.interfaces.ITournoiService;


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
        // Vérification d'existence de matchs pour ce tournoi
        if (tournoiADejaDesMatchs(idTournoi)) {
            throw new IllegalStateException("Un tirage au sort a déjà été effectué pour ce tournoi.");
        }

        Tournoi tournoi = tournoiRepository.findById(idTournoi)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        List<Equipe> equipes = getEquipesParTournoi(idTournoi);
        if (equipes.size() < 2) {
            throw new IllegalStateException("Il faut au moins 2 équipes pour organiser un tournoi.");
        }

        // Mélanger les équipes de manière aléatoire
        Collections.shuffle(equipes);

        List<MatchFo> matchs = new ArrayList<>();
        StringBuilder result = new StringBuilder("Tour 1 :\n");

        int matchCount = 1;
        for (int i = 0; i < equipes.size() - 1; i += 2) {
            Equipe equipe1 = equipes.get(i);
            Equipe equipe2 = equipes.get(i + 1);

            MatchFo match = new MatchFo();
            match.setNom("Match " + matchCount + " - Tour 1");
            match.setTournoi(tournoi);
            match.setTour(1);
            match.setDateMatch(null); // La date sera définie plus tard
            match.setHeureMatch(null); // L'heure sera définie plus tard
            match.setEquipes1(List.of(equipe1, equipe2));

            matchs.add(match);

            result.append(match.getNom()).append(" : ")
                    .append(equipe1.getNom()).append(" vs ")
                    .append(equipe2.getNom()).append("\n");

            matchCount++;
        }

        matchFoRepository.saveAll(matchs);

        return result.toString();
    }

    @Transactional

    public String mettreAJourScores(Integer matchId, int scoreEquipe1, int scoreEquipe2,
                                    int cj1, int cr1, int corner1,
                                    int cj2, int cr2, int corner2) {

        MatchFo match = matchFoRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match introuvable"));

        match.setScoreEquipe1(scoreEquipe1);
        match.setScoreEquipe2(scoreEquipe2);

        match.setCartonsJaunesEquipe1(cj1);
        match.setCartonsRougesEquipe1(cr1);
        match.setCornersEquipe1(corner1);

        match.setCartonsJaunesEquipe2(cj2);
        match.setCartonsRougesEquipe2(cr2);
        match.setCornersEquipe2(corner2);

        Equipe equipeGagnante = scoreEquipe1 > scoreEquipe2
                ? match.getEquipes1().get(0)
                : match.getEquipes1().get(1);

        matchFoRepository.save(match);

        return "Scores et statistiques mis à jour avec succès ! Équipe gagnante : " + equipeGagnante.getNom();
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

            // Date du match laissée vide
            match.setDateMatch(null);

            // Heure du match laissée vide
            match.setHeureMatch(null);

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




    @Transactional
    public String genererPlanningChampionnat(Integer idTournoi, boolean allerRetour) {
        Tournoi tournoi = tournoiRepository.findById(idTournoi)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        if (tournoiADejaDesMatchs(idTournoi)) {
            throw new IllegalStateException("Un planning a déjà été généré pour ce tournoi.");
        }

        List<Equipe> equipes = getEquipesParTournoi(idTournoi);
        int n = equipes.size();

        if (n < 2) {
            throw new IllegalStateException("Il faut au moins 2 équipes pour organiser un championnat.");
        }

        // Si nombre impair, on ajoute un "BYE" fictif pour avoir un nombre pair
        boolean nombreImpair = (n % 2 != 0);
        if (nombreImpair) {
            Equipe bye = new Equipe();
            bye.setNom("BYE");
            equipes.add(bye);  // ou une équipe fictive à ignorer dans les matchs
            n++;
        }

        List<MatchFo> matchs = new ArrayList<>();
        StringBuilder result = new StringBuilder("Planning du championnat" + (allerRetour ? " (aller-retour)" : "") + ":\n");

        int totalJournees = n - 1;
        int matchsParJournee = n / 2;
        List<Equipe> rotation = new ArrayList<>(equipes);

        // Phase aller
        result.append("Phase aller:\n");
        for (int journee = 0; journee < totalJournees; journee++) {
            result.append("Journée ").append(journee + 1).append(" :\n");

            for (int i = 0; i < matchsParJournee; i++) {
                Equipe equipe1 = rotation.get(i);
                Equipe equipe2 = rotation.get(n - 1 - i);

                if (!equipe1.getNom().equals("BYE") && !equipe2.getNom().equals("BYE")) {
                    MatchFo match = new MatchFo();
                    match.setNom("Journée " + (journee + 1) + " - Match " + (i + 1));
                    match.setTournoi(tournoi);
                    match.setTour(journee + 1);
                    match.setDateMatch(null);
                    match.setHeureMatch(null);
                    match.setEquipes1(List.of(equipe1, equipe2));
                    matchs.add(match);

                    result.append(equipe1.getNom()).append(" vs ").append(equipe2.getNom()).append("\n");
                }
            }

            // Rotation circulaire (sauf le premier)
            List<Equipe> nouvelleRotation = new ArrayList<>();
            nouvelleRotation.add(rotation.get(0)); // fixe
            nouvelleRotation.add(rotation.get(n - 1)); // dernier devient deuxième
            nouvelleRotation.addAll(rotation.subList(1, n - 1)); // le reste décale

            rotation = nouvelleRotation;
        }

        // Phase retour
        if (allerRetour) {
            result.append("Phase retour:\n");
            int baseJournee = totalJournees;

            for (int journee = 0; journee < totalJournees; journee++) {
                result.append("Journée ").append(baseJournee + journee + 1).append(" :\n");

                for (int i = 0; i < matchsParJournee; i++) {
                    Equipe equipe1 = rotation.get(i);
                    Equipe equipe2 = rotation.get(n - 1 - i);

                    if (!equipe1.getNom().equals("BYE") && !equipe2.getNom().equals("BYE")) {
                        MatchFo matchRetour = new MatchFo();
                        matchRetour.setNom("Journée " + (baseJournee + journee + 1) + " - Match " + (i + 1));
                        matchRetour.setTournoi(tournoi);
                        matchRetour.setTour(baseJournee + journee + 1);
                        matchRetour.setDateMatch(null);
                        matchRetour.setHeureMatch(null);
                        matchRetour.setEquipes1(List.of(equipe2, equipe1)); // ordre inversé
                        matchs.add(matchRetour);

                        result.append(equipe2.getNom()).append(" vs ").append(equipe1.getNom()).append("\n");
                    }
                }

                // Rotation circulaire pour retour
                List<Equipe> nouvelleRotation = new ArrayList<>();
                nouvelleRotation.add(rotation.get(0));
                nouvelleRotation.add(rotation.get(n - 1));
                nouvelleRotation.addAll(rotation.subList(1, n - 1));

                rotation = nouvelleRotation;
            }
        }

        matchFoRepository.saveAll(matchs);
        return result.toString();
    }

    public List<ClassementEquipeDTO> calculerClassement(Integer idTournoi) {
        List<MatchFo> matchs = matchFoRepository.findByTournoiIdTournoi(idTournoi);

        // Récupère le tournoi par son ID
        Tournoi tournoi = tournoiRepository.findById(idTournoi)
                .orElseThrow(() -> new RuntimeException("Tournoi non trouvé"));

        // Récupérer les équipes associées à ce tournoi via la méthode getEquipesParTournoi
        List<Equipe> equipes = tournoiEquipeRepository.findAllByTournoi(tournoi).stream()
                .map(TournoiEquipe::getEquipe)
                .collect(Collectors.toList());

        Map<Equipe, ClassementEquipeDTO> classementMap = new HashMap<>();

        // Initialiser toutes les équipes avec 0
        for (Equipe equipe : equipes) {
            classementMap.put(equipe, new ClassementEquipeDTO(equipe.getNom(), equipe.getLogo()));
        }

        // Parcours les matchs joués pour mettre à jour les statistiques
        for (MatchFo match : matchs) {
            if (match.getScoreEquipe1() == null || match.getScoreEquipe2() == null) {
                continue; // match pas encore joué
            }

            Equipe equipeA = match.getEquipes1().get(0);
            Equipe equipeB = match.getEquipes1().get(1);
            int scoreA = match.getScoreEquipe1();
            int scoreB = match.getScoreEquipe2();

            ClassementEquipeDTO statsA = classementMap.get(equipeA);
            ClassementEquipeDTO statsB = classementMap.get(equipeB);

            statsA.matchsJoues++;
            statsB.matchsJoues++;

            statsA.butsMarques += scoreA;
            statsA.butsEncaisses += scoreB;

            statsB.butsMarques += scoreB;
            statsB.butsEncaisses += scoreA;

            // Calcul des points
            if (scoreA > scoreB) {
                statsA.points += 3;
            } else if (scoreB > scoreA) {
                statsB.points += 3;
            } else {
                statsA.points += 1;
                statsB.points += 1;
            }
        }

        // Calculer la différence de buts pour chaque équipe
        classementMap.values().forEach(ClassementEquipeDTO::calculerDifferenceButs);

        // Tri par points décroissants, puis par différence de buts
        return classementMap.values().stream()
                .sorted(Comparator
                        .comparingInt(ClassementEquipeDTO::getPoints).reversed()
                        .thenComparing(Comparator.comparingInt(ClassementEquipeDTO::getDifferenceButs).reversed())
                        .thenComparing(Comparator.comparingInt(ClassementEquipeDTO::getButsMarques).reversed()))
                .collect(Collectors.toList());

    }

    @Override
    public List<Map<String, Object>> getStatistiquesParMatch(Integer matchId) {
        Optional<MatchFo> matchOpt = matchFoRepository.findById(matchId);
        if (matchOpt.isEmpty()) {
            throw new RuntimeException("Match non trouvé avec id : " + matchId);
        }
        MatchFo match = matchOpt.get();

        List<Map<String, Object>> stats = new ArrayList<>();

        List<Equipe> equipes = match.getEquipes1();
        if (equipes.size() < 2) {
            throw new RuntimeException("Match avec moins de 2 équipes");
        }

        Map<String, Object> equipe1Stats = new HashMap<>();
        equipe1Stats.put("matchId", match.getIdMatch());
        equipe1Stats.put("equipe", equipes.get(0).getNom());
        equipe1Stats.put("score", match.getScoreEquipe1());
        equipe1Stats.put("cartonsJaunes", match.getCartonsJaunesEquipe1());
        equipe1Stats.put("cartonsRouges", match.getCartonsRougesEquipe1());
        equipe1Stats.put("corners", match.getCornersEquipe1());

        Map<String, Object> equipe2Stats = new HashMap<>();
        equipe2Stats.put("matchId", match.getIdMatch());
        equipe2Stats.put("equipe", equipes.get(1).getNom());
        equipe2Stats.put("score", match.getScoreEquipe2());
        equipe2Stats.put("cartonsJaunes", match.getCartonsJaunesEquipe2());
        equipe2Stats.put("cartonsRouges", match.getCartonsRougesEquipe2());
        equipe2Stats.put("corners", match.getCornersEquipe2());

        stats.add(equipe1Stats);
        stats.add(equipe2Stats);

        return stats;
    }




}