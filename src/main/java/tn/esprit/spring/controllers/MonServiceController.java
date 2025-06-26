package tn.esprit.spring.controllers;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.dto.*;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ReservationRepository;
import tn.esprit.spring.services.implimentations.MonService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/mon-service")
@RequiredArgsConstructor
public class MonServiceController {

    private final MonService monService; // Mark as final for constructor injection
    private final ReservationRepository reservationRepository;


    @PostMapping("/remplir")
    public ResponseEntity<String> remplirEvenements() {
        monService.remplirEvenementsAVenir();
        return ResponseEntity.ok("Événements à venir remplis avec succès.");
    }
    @GetMapping("/evenavenir")
    public ResponseEntity<List<EvenementSimplifieDTO>> getEvenementsAVenirSimplifier() {
        List<EvenementSimplifieDTO> evenements = monService.getEvenementsAVenirSimplified();
        return ResponseEntity.ok(evenements);
    }

    @GetMapping("/avenir")
    public ResponseEntity<List<Evenement>> getEvenementsAVenir() {
        List<Evenement> evenements = monService.getEvenementsAVenir();
        return ResponseEntity.ok(evenements);
    }
    @GetMapping("/listermatches")
    public ResponseEntity<List<EvenementSimplifieDTO>> getEvenementsAVenirSimplified() {
        List<EvenementSimplifieDTO> evenements = monService.getEvenementsAVenirSimplified();
        return  ResponseEntity.ok(evenements);
    }
    @GetMapping("/tournois")
    public ResponseEntity<List<Tournoi>> getTournois() {
        List<Tournoi> tournois = monService.getTournois();
        return ResponseEntity.ok(tournois);
    }
    @GetMapping("/tournois/{tournoiId}/evenements")
    public ResponseEntity<List<EvenementSimplifieDTO>> getEvenementsByTournoi(@PathVariable int tournoiId) {
        List<EvenementSimplifieDTO> evenements = monService.getEvenementsByTournoi(tournoiId);
        return ResponseEntity.ok(evenements);
    }
    @GetMapping("/eve/{evenementId}")
    public ResponseEntity<List<Map<String, Object>>> getZonesByMatch(@PathVariable int evenementId) {
        List<Map<String, Object>> zones = monService.getZonesByEvenement(evenementId);
        return ResponseEntity.ok(zones);
    }

    @PostMapping("/evenement/{evenementId}")
    public ResponseEntity<?> reserverEvenement(
            @PathVariable int evenementId,
            @Valid @RequestBody ReservationRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Erreur de validation");
            response.put("data", null);
            response.put("errors", errors);

            return ResponseEntity.badRequest().body(response);
        }

        try {
            Reservation reservation = monService.reserverEvenement(
                    evenementId,
                    request.getZoneId(),
                    request.getNom(),
                    request.getPrenom(),
                    request.getEmail(),
                    request.getNombrePlaces()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Réservation enregistrée avec succès");
            response.put("data", Map.of(
                    "reservationId", reservation.getId(),
                    "placesReservees_en_attente_de_confirmation", reservation.getNombrePlaces(),
                    "placesRestantes", 5 - (reservationRepository.sumPlacesByEmailAndEtat(
                            request.getEmail(), EtatReservation.CONFIRMED))
            ));

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/confirmer/{id}")
    public ResponseEntity<?> confirmerReservation(
            @PathVariable("id") int reservationId,
            @RequestParam String email,
            @RequestHeader("Authorization") String token) {  // Ajout des paramètres manquants

        try {
            BilletResponseDTO response = monService.confirmerReservation(reservationId, email, token);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }
    @PostMapping("/tournoi/{tournoiId}")
    public ResponseEntity<?> reserverTournoi(
            @PathVariable int tournoiId,
            @Valid @RequestBody ReservationTournoiRequest request,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Erreur de validation");
            response.put("data", null);
            response.put("errors", errors);

            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<Reservation> reservations = monService.reserverTournoi(
                    tournoiId,
                    request.getNomZone(),
                    request.getNom(),
                    request.getPrenom(),
                    request.getEmail(),
                    request.getNombrePlacesParEvenement()
            );

            List<Map<String, Object>> data = reservations.stream().map(r -> Map.<String, Object>of(
                    "evenementId", r.getEvenement().getId(),
                    "zone", r.getZone().getNom(),
                    "nombrePlaces", r.getNombrePlaces(),
                    "nom", r.getNom(),
                    "prenom", r.getPrenom(),
                    "email", r.getEmail()
            )).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Réservation enregistrée avec succès");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/confirmer_tournoi")
    public ResponseEntity<AbonnementResponseDTO> confirmerAbonnement(
            @RequestParam int tournoiId,
            @RequestParam String email) {
        AbonnementResponseDTO dto = monService.confirmerAbonnementPourTournoi(tournoiId, email);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refuser")
    public ResponseEntity<String> refuserReservation(
            @RequestParam int idReservation,
            @RequestParam String email,
            @RequestHeader("Authorization") String token) {  // Ajout du token dans les headers

        try {
            monService.refuserReservationOuGroupe(idReservation, email, token);
            return ResponseEntity.ok("Réservation(s) refusée(s) avec succès.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }
    @GetMapping("/zones-abonnement-pleines/{tournoiId}")
    public ResponseEntity<Boolean> areAbonnementZonesFull(@PathVariable Integer tournoiId) {
        boolean full = monService.areAbonnementZonesFullForTournoi(tournoiId);
        return ResponseEntity.ok(full);
    }
}