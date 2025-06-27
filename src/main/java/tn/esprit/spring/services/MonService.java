package tn.esprit.spring.services;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.dto.AbonnementResponseDTO;
import tn.esprit.spring.dto.BilletResponseDTO;
import tn.esprit.spring.dto.EvenementSimplifieDTO;
import tn.esprit.spring.dto.MatchDetailDTO;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.interfaces.IMonService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonService implements IMonService {
    private final MatchFoRepository matchFoRepository;
    private final ZoneRepository zoneRepository;
    private final ReservationRepository reservationRepository;
    private final TournoiRepository tournoiRepository;
    private final BilletRepository billetRepository;
    private final EvenementRepository evenementRepository;
    private final AbonnementRepository abonnementRepository;
    private final EmailService emailService;
    private final TokenService tokenService;

//    @Scheduled(cron = "0 * * * * *") // Toutes les minutes
//    @Transactional
//    public void updateExpiredReservations() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Reservation> expiredReservations = reservationRepository
//                .findByEtatAndDateExpirationBefore(EtatReservation.EN_ATTENTE, now);
//
//        expiredReservations.forEach(reservation -> {
//            reservation.setEtat(EtatReservation.REFUSED);
//            reservationRepository.save(reservation);
//        });
//    }

//    @Scheduled(cron = "0 0 * * * *") // Toutes les heures
//    public void verifierEvenementsReservables() {
//        Date now = new Date();
//        List<Evenement> evenements = evenementRepository.findAll();
//
//        for (Evenement e : evenements) {
//            if (e.getHeureFin() != null && e.getHeureFin().before(now) && e.isReservable()) {
//                e.setReservable(false);
//            }
//        }
//
//        evenementRepository.saveAll(evenements);
//    }

    @Override
    public List<Evenement> getEvenementsAVenir() {
        LocalDate today = LocalDate.now();
        Date now = new Date();

        return evenementRepository.findAll().stream()
                .filter(e -> {
                    // Si l'événement est plus tard que maintenant
                    if (e.getDateDebut().isAfter(today)) return true;

                    // Si aujourd'hui = dateDebut, on vérifie si l'heureFin n'est pas encore passée
                    if (e.getDateDebut().isEqual(today) && e.getHeureFin() != null) {
                        return e.getHeureFin().after(now);
                    }

                    return false; // événements passés
                })
                .peek(e -> {
                    if (e.getNbSpectateurActuel() == 0) {
                        e.setNbSpectateurActuel(0);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Evenement> remplirEvenementsAVenir() {
        List<Evenement> allEvenements = new ArrayList<>();
        Date now = new Date();
        // Récupérer tous les matchs à venir
        List<MatchFo> matchs = matchFoRepository.findAll();

        for (MatchFo match : matchs) {
            // Vérifie si un événement existe déjà pour ce match
            Evenement existingEvent = evenementRepository.findByMatch(match);

            if (existingEvent != null) {
                if (existingEvent.getHeureFin() != null) {
                    if (existingEvent.getHeureFin().before(now) && existingEvent.isReservable()) {
                        // passé => inreservable
                        existingEvent.setReservable(false);
                        evenementRepository.save(existingEvent);
                        evenementRepository.flush();
                    } else if (existingEvent.getHeureFin().after(now) && !existingEvent.isReservable()) {
                        // futur => reservable
                        existingEvent.setReservable(true);
                        evenementRepository.save(existingEvent);
                        evenementRepository.flush();
                    }
                }
                continue; // ne recrée pas
            }
            if (isEventInFuture(match)) {
                boolean existeDeja = evenementRepository.existsByMatch(match);
                if (existeDeja) {
                    continue; // Ignorer les doublons
                }
                Evenement evenement = new Evenement();
                evenement.setNomEquipe1(match.getEquipes1().get(0).getNom());
                evenement.setNomEquipe2(match.getEquipes1().get(1).getNom());
                evenement.setTerrain(match.getTerrain());
                evenement.setDateDebut(match.getDateMatch());
                LocalDate date = match.getDateMatch(); // ex: 2025-05-23
                Date heureOnly = match.getHeureMatch(); // contient seulement l'heure (1970-01-01)

                LocalTime time = extractLocalTime(heureOnly); // Utilisation de la méthode extractLocalTime

                // Fusionner date + heure
                Date heureDebut = Date.from(date.atTime(time)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());

                // Calculer l'heure de fin
                Date heureFin = calculateEndTime(time, date);

                evenement.setHeureDebut(heureDebut);
                evenement.setHeureFin(heureFin); // nouvelle signature
                evenement.setNbSpectateurActuel(0); // Initialiser le nombre de spectateurs actuels
                evenement.setMatch(match);
                evenement.setReservable(heureFin.after(new Date()));

                // Vérifier si ce match appartient à un tournoi
                if (match.getTournoi() != null) {
                    evenement.setEstTournoi(true);
                    evenement.setTournoi(match.getTournoi());
                } else {
                    evenement.setEstTournoi(false);
                }

                // ✅ Sauvegarder d'abord l'événement pour obtenir un ID persistant
                evenement = evenementRepository.save(evenement);

                // Décomposer l'événement en 4 zones et initialiser les chaises
                createZones(evenement);

                // Ajouter l'événement à la liste
                allEvenements.add(evenement);
            }
        }

        // Eliminer les événements passés
        // Garder les événements à venir

        return allEvenements.stream()
                .filter(evenement -> evenement.getHeureFin().after(new Date())) // Garder les événements à venir
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEventInFuture(MatchFo match) {
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now(); // Heure actuelle en LocalTime

        // Convertir la date et l'heure du match en LocalDateTime
        LocalDateTime matchDateTime = LocalDateTime.of(match.getDateMatch(), extractLocalTime(match.getHeureMatch()));

        // Vérifier si la date du match est dans le futur
        if (match.getDateMatch().isAfter(today)) {
            return true;
        }

        // Si la date est aujourd'hui, on compare l'heure du match avec l'heure actuelle
        if (match.getDateMatch().isEqual(today)) {
            return matchDateTime.toLocalTime().isAfter(nowTime); // Comparer LocalTime avec LocalTime
        }

        return false;

    }

    @Override
    public Date calculateEndTime(LocalTime startTime, LocalDate date) {
        LocalTime endTime = startTime.plusHours(2);
        return Date.from(endTime.atDate(date)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public void createZones(Evenement evenement) {
        // Diviser le terrain en 4 zones : A, B, ABONNEMENT_A, ABONNEMENT_B
        NomZone[] zones = NomZone.values();

        int nbSpectateursParZone = evenement.getTerrain().getNbSpectateur() / zones.length;

        for (NomZone zoneNom : zones) {
            Zone zone = new Zone();
            zone.setNom(zoneNom);
            zone.setEvenement(evenement);
            zone.setCapaciteMax(nbSpectateursParZone); // définir la capacité max
            zone.setNbPlacesReservees(0); // aucune place réservée au départ

            zoneRepository.save(zone);
        }
    }

    @Override
    public LocalTime extractLocalTime(Date heureMatch) {
        if (heureMatch == null) return null;

        // Convertir java.sql.Time (heureMatch) en LocalTime
        long ms = heureMatch.getTime();
        return LocalTime.ofNanoOfDay(ms * 1_000_000);
    }
    @Transactional
    @Override
    public void deleteEvenementWithDependencies(Evenement evenement) {
        List<Zone> zones = zoneRepository.findByEvenement(evenement);

        // Plus besoin de supprimer les chaises
        zoneRepository.deleteAll(zones); // Supprimer toutes les zones de l'événement
        evenementRepository.delete(evenement); // Supprimer l'événement lui-même
        System.out.println("Supprimé: " + evenement.getId());
    }

    @Override
    public List<EvenementSimplifieDTO> getEvenementsAVenirSimplified() {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        return evenementRepository.findAll().stream()
                .filter(e -> e.getHeureFin().after(new Date()))
                .map(e -> {
                    EvenementSimplifieDTO dto = new EvenementSimplifieDTO();
                    dto.setId(e.getId());
                    dto.setNomEquipe1(e.getNomEquipe1());
                    dto.setNomEquipe2(e.getNomEquipe2());
                    dto.setNomTerrain(e.getTerrain().getNom());
                    dto.setAdresseTerrain(e.getTerrain().getAdresse());
                    dto.setDateDebut(e.getDateDebut());
                    if (e.getHeureDebut() != null) {
                        dto.setHeureDebut(timeFormatter.format(e.getHeureDebut()));
                    }

                    if (e.getHeureFin() != null) {
                        dto.setHeureFin(timeFormatter.format(e.getHeureFin()));
                    }
                    dto.setCapaciteMax(e.getTerrain().getNbSpectateur());
                    dto.setNbSpectateurActuel(e.getNbSpectateurActuel());
                    if (e.getTournoi() != null) {
                        dto.setNomTournoi(e.getTournoi().getNom());
                    }
                    if (e.getMatch() != null) {
                        // Ici on extrait uniquement le champ voulu du match
                        dto.setNomMatch(e.getMatch().getNom());  // Exemple
                    }
                    return dto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<Tournoi> getTournois() {
        return tournoiRepository.findAll().stream()
                .filter(evenementRepository::existsByTournoiAndReservableTrue)
                .collect(Collectors.toList());
    }
    @Override
    public List<EvenementSimplifieDTO> getEvenementsByTournoi(int tournoiId) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        return evenementRepository.findAll().stream()
                .filter(e -> e.getTournoi() != null && e.getTournoi().getIdTournoi() == tournoiId)
                .map(e -> {
                    EvenementSimplifieDTO dto = new EvenementSimplifieDTO();
                    dto.setId(e.getId());
                    dto.setNomEquipe1(e.getNomEquipe1());
                    dto.setNomEquipe2(e.getNomEquipe2());
                    dto.setNomTerrain(e.getTerrain().getNom());
                    dto.setAdresseTerrain(e.getTerrain().getAdresse());
                    dto.setDateDebut(e.getDateDebut());
                    if (e.getHeureDebut() != null) {
                        dto.setHeureDebut(timeFormatter.format(e.getHeureDebut()));
                    }

                    if (e.getHeureFin() != null) {
                        dto.setHeureFin(timeFormatter.format(e.getHeureFin()));
                    }

                    if (e.getMatch() != null) {
                        // Ici on extrait uniquement le champ voulu du match
                        dto.setNomMatch(e.getMatch().getNom());  // Exemple
                    }

                    if (e.getTournoi() != null) {
                        dto.setNomTournoi(e.getTournoi().getNom());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<Map<String, Object>> getZonesByEvenement(int evenementId) {
        Optional<Evenement> optionalEvenement = evenementRepository.findById(evenementId);
        if (optionalEvenement.isEmpty()) return Collections.emptyList();

        Evenement evenement = optionalEvenement.get();
        List<Map<String, Object>> zonesInfos = new ArrayList<>();

        for (Zone zone : evenement.getZones()) {

            Map<String, Object> zoneInfo = new HashMap<>();
            zoneInfo.put("id", zone.getId());
            zoneInfo.put("nomZone", zone.getNom().name());
            zoneInfo.put("capaciteMax", zone.getCapaciteMax());
            zoneInfo.put("nbPlacesReservees", zone.getNbPlacesReservees());
            zoneInfo.put("estPleine", zone.getNbPlacesReservees() >= zone.getCapaciteMax());

            zonesInfos.add(zoneInfo);
        }

        return zonesInfos;
    }



    @Override

    public boolean isZonePleine(Zone zone) {
        return zone.getNbPlacesReservees() >= zone.getCapaciteMax();
    }

    @Override
    @Transactional
    public Reservation reserverEvenement(int evenementId, int zoneId, String nom, String prenom, String email, int nombrePlaces) throws MessagingException {
        // Récupération de l'événement
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new EntityNotFoundException("Événement introuvable"));

        // Vérifier si l'événement est encore réservable
        if (!evenement.isReservable()) {
            throw new IllegalStateException("Cet événement n'est plus réservable.");
        }

        // Vérifier la limite max de places par email
        int totalPlacesReservees = reservationRepository.sumPlacesByEmailAndEtat(email, EtatReservation.CONFIRMED);
        if (totalPlacesReservees + nombrePlaces > 5) {
            throw new IllegalStateException("Vous avez déjà réservé " + totalPlacesReservees +
                    " places. La limite est de 5 places par personne.");
        }

        // Récupérer la zone
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone introuvable"));

        // Pour un tournoi, seules certaines zones sont autorisées
        if (evenement.getTournoi() != null &&
                !(zone.getNom() == NomZone.A || zone.getNom() == NomZone.B)) {
            throw new IllegalStateException("Pour un tournoi, seules les zones A et B sont autorisées.");
        }

        // Vérifier que la zone fait bien partie des zones de l'événement
        if (!evenement.getZones().contains(zone)) {
            throw new IllegalStateException("La zone ne correspond pas au terrain de l'événement.");
        }

        // Vérifier la capacité dans la zone pour cet événement
        int totalPlacesDansZone = reservationRepository.sumPlacesByEvenementAndZoneAndEtat(
                evenement, zone, EtatReservation.CONFIRMED);
        if (totalPlacesDansZone + nombrePlaces > zone.getCapaciteMax()) {
            throw new IllegalStateException("Pas assez de places disponibles dans cette zone.");
        }

        // Vérifier la capacité maximale globale de l'événement
        int totalPlacesDansEvenement = reservationRepository.sumPlacesByEvenementAndEtat(
                evenement, EtatReservation.CONFIRMED);
        int capaciteMaxEvenement = evenement.getTerrain().getNbSpectateur();
        if (totalPlacesDansEvenement + nombrePlaces > capaciteMaxEvenement) {
            throw new IllegalStateException("Capacité maximale de l’événement atteinte.");
        }

        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setNom(nom);
        reservation.setPrenom(prenom);
        reservation.setEmail(email);
        reservation.setZone(zone);
        reservation.setEvenement(evenement);
        reservation.setNombrePlaces(nombrePlaces);
        reservation.setEtat(EtatReservation.EN_ATTENTE);
        reservation.setDateCreation(LocalDateTime.now());
        reservation.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        // Génération et stockage du token
        String token = tokenService.generateToken();
        reservation.setToken(token);

        // Sauvegarde en base
        Reservation saved = reservationRepository.save(reservation);

        // Envoi de l'email de confirmation (avec URL sans token dans l'URL)
        emailService.sendConfirmationEmail(saved);

        return saved;
    }


    @Override
    @Transactional
    public BilletResponseDTO confirmerReservation(int reservationId,String email, String token) {
        // 1. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable"));

        // Vérifier que l'email correspond
        if (!reservation.getEmail().equals(email)) {
            throw new IllegalStateException("Email non associé à cette réservation.");
        }

        if (reservation.getEtat() != EtatReservation.EN_ATTENTE) {
            throw new IllegalStateException("La réservation n'est pas en attente et ne peut pas être confirmée.");
        }
        if (reservation.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("La réservation a expiré et ne peut plus être confirmée.");
        }
        // 2. Récupérer la zone et l'événement
        Zone zone = reservation.getZone();
        Evenement evenement = reservation.getEvenement();
        int nombrePlaces = reservation.getNombrePlaces();

        // 3. Vérifier si l’événement est toujours réservable
        if (!evenement.isReservable()) {
            throw new IllegalStateException("Cet événement n'est plus réservable.");
        }

        // 4. Vérifier les places disponibles dans la zone
        int placesZone = zone.getNbPlacesReservees() + nombrePlaces;
        if (placesZone > zone.getCapaciteMax()) {
            throw new IllegalStateException("Pas assez de places disponibles dans la zone.");
        }

        // 5. Vérifier les places disponibles dans l’événement
        int placesEvent = evenement.getNbSpectateurActuel() + nombrePlaces;
        if (placesEvent > evenement.getTerrain().getNbSpectateur()) {
            throw new IllegalStateException("Capacité maximale de l’événement atteinte.");
        }
        // 6. Tout est bon : confirmer la réservation
        reservation.setEtat(EtatReservation.CONFIRMED);
        reservation.setDateReservation(LocalDateTime.now());
        reservationRepository.save(reservation);

        // 7. Mettre à jour la zone et l’événement
        zone.setNbPlacesReservees(placesZone);
        zoneRepository.save(zone);

        evenement.setNbSpectateurActuel(placesEvent);
        evenementRepository.save(evenement);

        // 8. Créer le billet
        Billet billet = new Billet();
        billet.setDateDeCreation(LocalDate.now());
        billet.setReservation(reservation);
        billet.setEtat(Etat_Presence.NOT_PRESENT);

        billetRepository.save(billet);

        // Envoi mail de confirmation
        emailService.sendConfirmationMail(reservation);

        // Construire la réponse personnalisée
        BilletResponseDTO response = new BilletResponseDTO();
        response.setNom(reservation.getNom());
        response.setPrenom(reservation.getPrenom());
        response.setEmail(reservation.getEmail());
        response.setZone(reservation.getZone().getNom().toString());

        MatchFo match = evenement.getMatch();
        if (match != null && match.getEquipes1() != null && match.getEquipes1().size() >= 2) {
            response.setNomEquipe1(match.getEquipes1().get(0).getNom());
            response.setLogoEquipe1(match.getEquipes1().get(0).getLogo());
            response.setNomEquipe2(match.getEquipes1().get(1).getNom());
            response.setLogoEquipe2(match.getEquipes1().get(1).getLogo());
            response.setNomMatch(match.getNom());
            response.setNomTerain(match.getTerrain().getNom());
            response.setAdressTerain(match.getTerrain().getAdresse());
            if(match.getTournoi() != null) {
                response.setNomTournoi(match.getTournoi().getNom());
            }
        }

        return response;
    }

    @Override
    @Transactional
    public List<Reservation> reserverTournoi(int tournoiId, NomZone nomZone, String nom, String prenom, String email, int nombrePlacesParEvenement) {
        // 1. Récupérer le tournoi
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new EntityNotFoundException("Tournoi introuvable"));

        // 2. Vérifier que la zone est autorisée
        if (nomZone != NomZone.ABONNEMENT_A && nomZone != NomZone.ABONNEMENT_B) {
            throw new IllegalStateException("Pour un tournoi, seules les zones ABONNEMENT_A et ABONNEMENT_B sont autorisées.");
        }

        // 3. Récupérer les événements du tournoi
        List<Evenement> evenements = evenementRepository.findByTournoi(tournoi);
        if (evenements.isEmpty()) {
            throw new IllegalStateException("Aucun événement trouvé pour ce tournoi.");
        }

        List<Reservation> reservationsCreees = new ArrayList<>();

        for (Evenement evenement : evenements) {
            // Récupérer la zone correspondante au nomZone dans cet événement
            Zone zoneCible = evenement.getZones().stream()
                    .filter(z -> z.getNom() == nomZone)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Zone " + nomZone + " non trouvée dans l'événement " + evenement.getId()));

            // Vérifier que l'événement est réservable
            if (!evenement.isReservable()) {
                throw new IllegalStateException("L'événement " + evenement.getId() + " n'est plus réservable.");
            }

            // Vérifier les capacités de la zone
            int placesReserveesZone = reservationRepository.sumPlacesByEvenementAndZoneAndEtat(
                    evenement, zoneCible, EtatReservation.CONFIRMED);

            if (placesReserveesZone + nombrePlacesParEvenement > zoneCible.getCapaciteMax()) {
                throw new IllegalStateException("Pas assez de places dans la zone " + nomZone + " pour l'événement " + evenement.getId());
            }

            // Vérifier la capacité totale de l’événement
            int placesReserveesEvenement = reservationRepository.sumPlacesByEvenementAndEtat(
                    evenement, EtatReservation.CONFIRMED);
            int capaciteMaxEvenement = evenement.getTerrain().getNbSpectateur();

            if (placesReserveesEvenement + nombrePlacesParEvenement > capaciteMaxEvenement) {
                throw new IllegalStateException("Capacité maximale de l'événement " + evenement.getId() + " atteinte.");
            }

            // Vérifier limite des places par email
            int totalPlacesReserveesEmail = reservationRepository.sumPlacesByEmailAndEtat(email, EtatReservation.CONFIRMED);
            if (totalPlacesReserveesEmail + nombrePlacesParEvenement > 5) {
                throw new IllegalStateException("Limite de 5 places par personne dépassée.");
            }

            // Créer la réservation
            Reservation reservation = new Reservation();
            reservation.setNom(nom);
            reservation.setPrenom(prenom);
            reservation.setEmail(email);
            reservation.setZone(zoneCible);
            reservation.setEvenement(evenement);
            reservation.setNombrePlaces(nombrePlacesParEvenement);
            reservation.setEtat(EtatReservation.EN_ATTENTE);
            reservation.setDateCreation(LocalDateTime.now());
            reservation.setDateExpiration(LocalDateTime.now().plusMinutes(5));

            reservationsCreees.add(reservationRepository.save(reservation));
        }

        return reservationsCreees;
    }

    @Override
    @Transactional
    public AbonnementResponseDTO confirmerAbonnementPourTournoi(int tournoiId, String email) {
        // 1. Récupérer le tournoi
        Tournoi tournoi = tournoiRepository.findById(tournoiId)
                .orElseThrow(() -> new EntityNotFoundException("Tournoi introuvable"));

        // 2. Récupérer les réservations en attente pour ce tournoi et cet email
        List<Reservation> reservations = reservationRepository.findByEmailAndEtatAndEvenement_Tournoi(
                email, EtatReservation.EN_ATTENTE, tournoi
        );

        if (reservations.isEmpty()) {
            throw new IllegalStateException("Aucune réservation en attente trouvée pour ce tournoi et cet email.");
        }

        // 3. Valider et confirmer chaque réservation
        List<MatchDetailDTO> matchs = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Evenement evenement = reservation.getEvenement();
            Zone zone = reservation.getZone();
            int nbPlaces = reservation.getNombrePlaces();

            // Vérifier les capacités
            if (zone.getNbPlacesReservees() + nbPlaces > zone.getCapaciteMax()) {
                throw new IllegalStateException("Capacité insuffisante dans la zone pour l'événement " + evenement.getId());
            }
            if (evenement.getNbSpectateurActuel() + nbPlaces > evenement.getTerrain().getNbSpectateur()) {
                throw new IllegalStateException("Capacité maximale de l'événement " + evenement.getId() + " atteinte.");
            }
            if (reservation.getDateExpiration().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("La réservation " + reservation.getId() + " a expiré et ne peut plus être confirmée.");
            }

            // Confirmer
            reservation.setEtat(EtatReservation.CONFIRMED);
            reservation.setDateReservation(LocalDateTime.now());
            reservationRepository.save(reservation);

            // Mettre à jour zone et événement
            zone.setNbPlacesReservees(zone.getNbPlacesReservees() + nbPlaces);
            evenement.setNbSpectateurActuel(evenement.getNbSpectateurActuel() + nbPlaces);
            zoneRepository.save(zone);
            evenementRepository.save(evenement);

            // Construire les détails du match
            MatchFo match = evenement.getMatch();
            if (match != null && match.getEquipes1() != null && match.getEquipes1().size() >= 2) {
                MatchDetailDTO matchDTO = new MatchDetailDTO();
                matchDTO.setNomMatch(match.getNom());
                matchDTO.setNomEquipe1(match.getEquipes1().get(0).getNom());
                matchDTO.setLogoEquipe1(match.getEquipes1().get(0).getLogo());
                matchDTO.setNomEquipe2(match.getEquipes1().get(1).getNom());
                matchDTO.setLogoEquipe2(match.getEquipes1().get(1).getLogo());
                matchDTO.setNomTerrain(match.getTerrain().getNom());
                matchDTO.setAdresseTerrain(match.getTerrain().getAdresse());
                matchs.add(matchDTO);
            }
        }

        // 4. Créer l'abonnement et lier les réservations
        Abonnement abonnement = new Abonnement();
        abonnement.setDateDeCreation(LocalDate.now());
        abonnement.setEtat(Etat_Presence.NOT_PRESENT);
        abonnement.setTournoi(tournoi);

        for (Reservation r : reservations) {
            r.setAbonnement(abonnement); // Lier côté propriétaire
        }
        abonnement.setReservations(reservations); // Lier côté inverse (utile pour la cohérence objet)

        // 5. Sauvegarder une seule fois
        abonnementRepository.save(abonnement); // Cascade.ALL va enregistrer les réservations


        // 5. Construire et retourner le DTO
        Reservation resRef = reservations.get(0);
        AbonnementResponseDTO response = new AbonnementResponseDTO();
        response.setNom(resRef.getNom());
        response.setPrenom(resRef.getPrenom());
        response.setEmail(resRef.getEmail());
        response.setNomTournoi(tournoi.getNom());
        response.setMatchs(matchs);

        return response;
    }

    @Override
    public void refuserReservationOuGroupe(int idReservation, String email,String token) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));

        if (!reservation.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email non associé à cette réservation");
        }

        Evenement evenement = reservation.getEvenement();
        if (evenement.getTournoi() == null) {
            // Réservation simple
            reservation.setEtat(EtatReservation.REFUSED);
            reservationRepository.save(reservation);

            // Envoi mail refus pour simple réservation
            emailService.sendRefusalMail(reservation);
        } else {
            // Groupe de réservations pour un tournoi
            List<Reservation> reservationsTournoi = reservationRepository
                    .findByEvenement_Tournoi_IdTournoiAndEmail(evenement.getTournoi().getIdTournoi(), reservation.getEmail());

            for (Reservation r : reservationsTournoi) {
                r.setEtat(EtatReservation.REFUSED);
            }

            reservationRepository.saveAll(reservationsTournoi);

            // Envoi mail refus pour chaque réservation du groupe
            for (Reservation r : reservationsTournoi) {
                emailService.sendRefusalMail(r);
            }
        }
    }

    @Override
    public boolean areAbonnementZonesFullForTournoi(Integer tournoiId) {
        // Récupérer tous les événements liés à ce tournoi
        List<Evenement> evenements = evenementRepository.findByTournoi_IdTournoi(tournoiId);

        for (Evenement evenement : evenements) {
            // Récupérer les zones ABONNEMENT_A et ABONNEMENT_B pour cet événement
            List<Zone> zones = zoneRepository.findByEvenementAndNomIn(
                    evenement,
                    List.of(NomZone.ABONNEMENT_A, NomZone.ABONNEMENT_B)
            );

            // Vérifier que chaque zone est pleine
            for (Zone zone : zones) {
                if (!zone.isEstPleine()) {
                    return false; // Au moins une zone n'est pas pleine
                }
            }
        }

        return true; // Toutes les zones sont pleines pour tous les événements du tournoi
    }

    @Override
    public void updateZonePleineStatus(Zone zone) {
        zone.setEstPleine(zone.getNbPlacesReservees() >= zone.getCapaciteMax());
        zoneRepository.save(zone);
    }


}


