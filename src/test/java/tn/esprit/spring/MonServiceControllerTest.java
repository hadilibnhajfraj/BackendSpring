package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import tn.esprit.spring.dto.BilletResponseDTO;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.EmailService;
import tn.esprit.spring.services.MonService;
import tn.esprit.spring.services.TokenService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

class MonServiceTest {

    @InjectMocks
    MonService service;

    @Mock ZoneRepository zoneRepo;
    @Mock ReservationRepository reservationRepo;
    @Mock TournoiRepository tournoiRepo;
    @Mock EvenementRepository evtRepo;
    @Mock BilletRepository billetRepo;
    @Mock
    EmailService emailService;
    @Mock
    TokenService tokenService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // --- utilitaires de date/heure ---

    @Test
    void calculateEndTime_addsTwoHours() {
        LocalDate date = LocalDate.of(2025,6,27);
        LocalTime start = LocalTime.of(9,15);
        Date end = service.calculateEndTime(start, date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(15, cal.get(Calendar.MINUTE));
    }

    @Test
    void extractLocalTime_null_returnsNull() {
        assertNull(service.extractLocalTime(null));
    }

    @Test
    void extractLocalTime_fromTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // Optional
        Date d = java.sql.Time.valueOf(LocalTime.of(14, 30));
        LocalTime t = service.extractLocalTime(d);
        assertEquals(LocalTime.of(14, 30), t);
    }

    @Test
    void isEventInFuture_futureDate_returnsTrue() {
        // Mock de l'objet MatchFo
        MatchFo m = mock(MatchFo.class);

        // Configurer les comportements attendus du mock
        when(m.getDateMatch()).thenReturn(LocalDate.now().plusDays(1)); // Date future
        when(m.getHeureMatch()).thenReturn(java.sql.Time.valueOf("10:00:00")); // Heure sous forme de java.util.Date

        // Appel de la méthode à tester
        boolean ok = service.isEventInFuture(m);

        // Vérifier que le résultat est correct
        assertTrue(ok, "Une date future devrait renvoyer true");
    }

    @Test
    void isEventInFuture_todayPastTime_returnsFalse() {
        MatchFo m = mock(MatchFo.class);
        when(m.getDateMatch()).thenReturn(LocalDate.now());
        when(m.getHeureMatch()).thenReturn(java.sql.Time.valueOf(LocalTime.now().minusHours(1)));
        assertFalse(service.isEventInFuture(m));
    }

    // --- getEvenementsAVenir ---

    @Test
    void getEvenementsAVenir_filtersCorrectly() {
        Evenement past = new Evenement();
        past.setDateDebut(LocalDate.now().minusDays(1));
        past.setHeureFin(new Date(System.currentTimeMillis() - 1000));
        Evenement fut = new Evenement();
        fut.setDateDebut(LocalDate.now().plusDays(1));
        fut.setHeureFin(new Date(System.currentTimeMillis() + 1000));
        when(evtRepo.findAll()).thenReturn(List.of(past, fut));

        List<Evenement> l = service.getEvenementsAVenir();
        assertEquals(1, l.size());
        assertSame(fut, l.get(0));
    }

    // --- createZones & deleteEvenementWithDependencies ---

    @Test
    void createZones_saves4Zones() {
        Evenement e = new Evenement();
        Terrain t = new Terrain(); t.setNbSpectateur(100);
        e.setTerrain(t);
        service.createZones(e);
        // 4 zones enum
        verify(zoneRepo, times(NomZone.values().length)).save(any());
    }

    @Test
    void deleteEvenementWithDependencies_deletesZonesAndEvt() {
        Evenement e = new Evenement(); e.setId(5);
        Zone z1 = new Zone(), z2 = new Zone();
        when(zoneRepo.findByEvenement(e)).thenReturn(List.of(z1, z2));
        service.deleteEvenementWithDependencies(e);
        verify(zoneRepo).deleteAll(List.of(z1, z2));
        verify(evtRepo).delete(e);
    }

    // --- reserverEvenement ---

    @Test
    void reserverEvenement_success() throws MessagingException {
        // Initialisation de l'objet Evenement avec une liste de zones
        Evenement e = new Evenement();
        e.setReservable(true);
        e.setZones(new ArrayList<>()); // Initialisation de la liste des zones

        // Initialisation de l'objet Terrain
        Terrain t = new Terrain();
        t.setNbSpectateur(100); // Exemple de nombre maximum de spectateurs
        e.setTerrain(t);

        // Création et ajout d'une zone dans la liste
        Zone z = new Zone();
        z.setId(2);
        z.setCapaciteMax(10);
        z.setNbPlacesReservees(0);
        e.getZones().add(z);

        // Configuration des mocks
        when(evtRepo.findById(1)).thenReturn(Optional.of(e));
        when(reservationRepo.sumPlacesByEmailAndEtat("a@b.c", EtatReservation.CONFIRMED)).thenReturn(0);
        when(zoneRepo.findById(2)).thenReturn(Optional.of(z));
        when(reservationRepo.sumPlacesByEvenementAndZoneAndEtat(e, z, EtatReservation.CONFIRMED)).thenReturn(0);
        when(reservationRepo.sumPlacesByEvenementAndEtat(e, EtatReservation.CONFIRMED)).thenReturn(0);
        when(tokenService.generateToken()).thenReturn("tok");

        // Capturer l'argument passé à la méthode save
        ArgumentCaptor<Reservation> cap = ArgumentCaptor.forClass(Reservation.class);
        when(reservationRepo.save(cap.capture())).thenAnswer(i -> i.getArgument(0));

        // Appel de la méthode à tester
        Reservation r = service.reserverEvenement(1, 2, "N", "P", "a@b.c", 3);

        // Vérifications des résultats
        assertEquals(3, r.getNombrePlaces());
        assertEquals("tok", r.getToken());
        verify(emailService).sendConfirmationEmail(r);
    }


    @Test
    void reserverEvenement_evtNotFound_throws() {
        when(evtRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                ()-> service.reserverEvenement(99,1,"","","",1));
    }

    // --- confirmerReservation ---

    @Test
    void confirmerReservation_success() {
        // Création de la réservation
        Reservation res = new Reservation();
        res.setId(7);
        res.setEmail("u@u");
        res.setEtat(EtatReservation.EN_ATTENTE);
        res.setDateExpiration(LocalDateTime.now().plusMinutes(10));

        // Création et association de l'événement
        Evenement e = new Evenement();
        e.setReservable(true);
        e.setNbSpectateurActuel(0);

        // Création et association du terrain
        Terrain t = new Terrain();
        t.setNbSpectateur(0);
        e.setTerrain(t);

        res.setEvenement(e);

        // Création et association de la zone (avec le nom !)
        Zone z = new Zone();
        z.setNbPlacesReservees(0);
        z.setCapaciteMax(5);
        z.setNom(NomZone.A);  // <-- correction ici !

        res.setZone(z);

        // Mocks
        when(reservationRepo.findById(7)).thenReturn(Optional.of(res));
        when(zoneRepo.save(any())).thenReturn(z);
        when(evtRepo.save(any())).thenReturn(e);
        when(billetRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution du service
        BilletResponseDTO dto = service.confirmerReservation(7, "u@u", "tok");

        // Vérification
        assertEquals("u@u", dto.getEmail());
    }



    @Test
    void confirmerReservation_notFound_throws() {
        when(reservationRepo.findById(8)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                ()-> service.confirmerReservation(8,"x","t"));
    }

    // --- reserverTournoi & confirmerAbonnementPourTournoi ---

    @Test
    void reserverTournoi_invalidZone_throws() {
        Tournoi tr = new Tournoi(); tr.setIdTournoi(3);
        when(tournoiRepo.findById(3)).thenReturn(Optional.of(tr));
        assertThrows(IllegalStateException.class,
                ()-> service.reserverTournoi(3, NomZone.A, "n","p","e@e",1));
    }

    @Test
    void areAbonnementZonesFullForTournoi_trueFalse() {
        Tournoi tr = new Tournoi(); tr.setIdTournoi(4);
        Evenement e1 = new Evenement(); Evenement e2 = new Evenement();
        when(evtRepo.findByTournoi_IdTournoi(4)).thenReturn(List.of(e1,e2));
        Zone zFull = mock(Zone.class);
        when(zFull.isEstPleine()).thenReturn(true);
        when(zoneRepo.findByEvenementAndNomIn(eq(e1), anyList())).thenReturn(List.of(zFull));
        when(zoneRepo.findByEvenementAndNomIn(eq(e2), anyList())).thenReturn(List.of(zFull));
        assertTrue(service.areAbonnementZonesFullForTournoi(4));

        when(zoneRepo.findByEvenementAndNomIn(eq(e2), anyList())).thenReturn(List.of(mock(Zone.class)));
        assertFalse(service.areAbonnementZonesFullForTournoi(4));
    }

    @Test
    void updateZonePleineStatus_setsFlagAndSaves() {
        Zone z = new Zone(); z.setCapaciteMax(2); z.setNbPlacesReservees(2);
        service.updateZonePleineStatus(z);
        assertTrue(z.isEstPleine());
        verify(zoneRepo).save(z);
    }
}
