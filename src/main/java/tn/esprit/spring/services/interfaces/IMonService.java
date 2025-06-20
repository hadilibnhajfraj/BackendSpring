package tn.esprit.spring.services.interfaces;

import jakarta.mail.MessagingException;
import tn.esprit.spring.dto.AbonnementResponseDTO;
import tn.esprit.spring.dto.BilletResponseDTO;
import tn.esprit.spring.dto.EvenementSimplifieDTO;
import tn.esprit.spring.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IMonService {

    List<Evenement> getEvenementsAVenir();
    List<Evenement> remplirEvenementsAVenir();
    boolean isEventInFuture(MatchFo match);
    Date calculateEndTime(LocalTime startTime, LocalDate date);
    void createZones(Evenement evenement);
    LocalTime extractLocalTime(Date heureMatch);
    void deleteEvenementWithDependencies(Evenement evenement);
    List<EvenementSimplifieDTO> getEvenementsAVenirSimplified();
    List<Tournoi> getTournois();
    List<EvenementSimplifieDTO> getEvenementsByTournoi(int tournoiId);
    List<Map<String, Object>> getZonesByEvenement(int evenementId);
    boolean isZonePleine(Zone zone);
    Reservation reserverEvenement(int evenementId, int zoneId, String nom, String prenom, String email, int nombrePlaces) throws MessagingException;
    BilletResponseDTO confirmerReservation(int reservationId,String email, String token);
    List<Reservation> reserverTournoi(int tournoiId, NomZone nomZone, String nom, String prenom, String email, int nombrePlacesParEvenement);
    AbonnementResponseDTO confirmerAbonnementPourTournoi(int tournoiId, String email);
    void refuserReservationOuGroupe(int idReservation, String email,String token);
    boolean areAbonnementZonesFullForTournoi(Integer tournoiId);
    void updateZonePleineStatus(Zone zone);

}
