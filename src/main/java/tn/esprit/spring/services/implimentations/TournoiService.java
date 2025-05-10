package tn.esprit.spring.services.implimentations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.interfaces.ITournoiService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;





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

    public byte[] genererRecapQRCode(Integer idTournoi) throws Exception {
        Tournoi tournoi = tournoiRepository.findById(idTournoi)
                .orElseThrow(() -> new NoSuchElementException("Tournoi introuvable"));

        List<MatchFo> matchs = matchFoRepository.findByTournoiIdTournoi(idTournoi);

        if (matchs.isEmpty()) {
            throw new IllegalStateException("Aucun match trouvé pour ce tournoi.");
        }

        Map<Equipe, Integer> butsParEquipe = new HashMap<>();
        Map<Equipe, Integer> butsEncaisses = new HashMap<>();
        Map<Equipe, Integer> victoires = new HashMap<>();
        Map<Equipe, Integer> defaites = new HashMap<>();
        Set<Equipe> equipesEncoreEnJeu = new HashSet<>();

        for (MatchFo match : matchs) {
            if (match.getScoreEquipe1() != null && match.getScoreEquipe2() != null) {
                List<Equipe> equipes = match.getEquipes1();
                if (equipes.size() != 2) continue;

                Equipe eq1 = equipes.get(0);
                Equipe eq2 = equipes.get(1);
                int score1 = match.getScoreEquipe1();
                int score2 = match.getScoreEquipe2();

                // Buts marqués
                butsParEquipe.put(eq1, butsParEquipe.getOrDefault(eq1, 0) + score1);
                butsParEquipe.put(eq2, butsParEquipe.getOrDefault(eq2, 0) + score2);

                // Buts encaissés
                butsEncaisses.put(eq1, butsEncaisses.getOrDefault(eq1, 0) + score2);
                butsEncaisses.put(eq2, butsEncaisses.getOrDefault(eq2, 0) + score1);

                // Résultats
                if (score1 > score2) {
                    victoires.put(eq1, victoires.getOrDefault(eq1, 0) + 1);
                    defaites.put(eq2, defaites.getOrDefault(eq2, 0) + 1);
                    equipesEncoreEnJeu.add(eq1);
                    equipesEncoreEnJeu.remove(eq2);
                } else if (score2 > score1) {
                    victoires.put(eq2, victoires.getOrDefault(eq2, 0) + 1);
                    defaites.put(eq1, defaites.getOrDefault(eq1, 0) + 1);
                    equipesEncoreEnJeu.add(eq2);
                    equipesEncoreEnJeu.remove(eq1);
                }
            }
        }

        if (equipesEncoreEnJeu.size() != 1) {
            throw new IllegalStateException("Le tournoi n'est pas encore terminé.");
        }

        Equipe gagnante = equipesEncoreEnJeu.iterator().next();

        int totalButs = butsParEquipe.values().stream().mapToInt(Integer::intValue).sum();
        int totalMatchs = matchs.size();
        double moyenneButsParMatch = totalMatchs > 0 ? (double) totalButs / totalMatchs : 0.0;

        Equipe meilleureAttaque = butsParEquipe.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        Equipe meilleureDefense = butsEncaisses.entrySet().stream()
                .min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        // Construction du résumé
        StringBuilder recap = new StringBuilder();
        recap.append("Tournoi : ").append(tournoi.getNom()).append("\n");
        recap.append("Équipe gagnante : ").append(gagnante.getNom()).append("\n");
        recap.append("Total des buts : ").append(totalButs).append("\n");
        recap.append("Moyenne buts / match : ").append(String.format("%.2f", moyenneButsParMatch)).append("\n");
        recap.append("Meilleure attaque : ").append(meilleureAttaque != null ? meilleureAttaque.getNom() : "N/A").append("\n");
        recap.append("Meilleure défense : ").append(meilleureDefense != null ? meilleureDefense.getNom() : "N/A").append("\n");
        recap.append("Statistiques par équipe :\n");

        Set<Equipe> toutesLesEquipes = new HashSet<>();
        toutesLesEquipes.addAll(butsParEquipe.keySet());
        toutesLesEquipes.addAll(butsEncaisses.keySet());

        for (Equipe equipe : toutesLesEquipes) {
            int buts = butsParEquipe.getOrDefault(equipe, 0);
            int encaisses = butsEncaisses.getOrDefault(equipe, 0);
            int diff = buts - encaisses;
            int win = victoires.getOrDefault(equipe, 0);
            int lose = defaites.getOrDefault(equipe, 0);
            recap.append("- ").append(equipe.getNom())
                    .append(" | Buts: ").append(buts)
                    .append(", Encaissés: ").append(encaisses)
                    .append(", Diff: ").append(diff)
                    .append(", V: ").append(win)
                    .append(", D: ").append(lose)
                    .append("\n");
        }

        return genererQRCode(recap.toString(), 300, 300);
    }

    public byte[] genererQRCode(String data, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
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
            classementMap.put(equipe, new ClassementEquipeDTO(equipe.getNom()));
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


    }