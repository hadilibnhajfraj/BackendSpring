package tn.esprit.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.Tournoi;
import tn.esprit.spring.services.interfaces.ITournoiService;
import tn.esprit.spring.repositories.MatchFoRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TournoiControllerTest {

    @InjectMocks
    private TournoiController tournoiController;

    @Mock
    private ITournoiService tournoiService;

    @Mock
    private MatchFoRepository matchFoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTournoi_success() {
        Tournoi tournoi = new Tournoi();
        tournoi.setNom("Coupe Printemps");
        tournoi.setDateDebut(LocalDate.of(2025, 6, 1));

        when(tournoiService.createTournoi(any(Tournoi.class))).thenReturn(tournoi);

        ResponseEntity<Tournoi> response = tournoiController.createTournoi(tournoi);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Coupe Printemps", response.getBody().getNom());

        verify(tournoiService, times(1)).createTournoi(any(Tournoi.class));
    }

    @Test
    public void testGetTournoiById_success() {
        Tournoi tournoi = new Tournoi();
        tournoi.setIdTournoi(1);
        tournoi.setNom("Tournoi Test");

        when(tournoiService.getTournoiById(1)).thenReturn(tournoi);

        ResponseEntity<Tournoi> response = tournoiController.getTournoiById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Tournoi Test", response.getBody().getNom());
        verify(tournoiService, times(1)).getTournoiById(1);
    }

    @Test
    public void testDeleteTournoi_success() {
        // deleteTournoi est void, on ne simule pas de retour

        ResponseEntity<Void> response = tournoiController.deleteTournoi(1);

        assertEquals(204, response.getStatusCodeValue());
        verify(tournoiService, times(1)).deleteTournoi(1);
    }

    @Test
    public void testGetAllTournois_success() {
        Tournoi t1 = new Tournoi();
        t1.setIdTournoi(1);
        Tournoi t2 = new Tournoi();
        t2.setIdTournoi(2);

        List<Tournoi> liste = Arrays.asList(t1, t2);

        when(tournoiService.getAllTournois()).thenReturn(liste);

        List<Tournoi> result = tournoiController.getAllTournois();

        assertEquals(2, result.size());
        verify(tournoiService, times(1)).getAllTournois();
    }

    @Test
    public void testUpdateTournoi_success() {
        Tournoi tournoi = new Tournoi();
        tournoi.setNom("Nom avant");
        tournoi.setDateDebut(LocalDate.of(2025, 1, 1));

        Tournoi tournoiUpdate = new Tournoi();
        tournoiUpdate.setNom("Nom après");
        tournoiUpdate.setDateDebut(LocalDate.of(2025, 2, 2));

        when(tournoiService.updateTournoi(eq(1), any(Tournoi.class))).thenReturn(tournoiUpdate);

        ResponseEntity<Tournoi> response = tournoiController.updateTournoi(1, tournoiUpdate);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Nom après", response.getBody().getNom());
        verify(tournoiService, times(1)).updateTournoi(eq(1), any(Tournoi.class));
    }

    @Test
    public void testAffecterEquipesATournoi_success() throws Exception {
        Tournoi tournoi = new Tournoi();
        tournoi.setIdTournoi(1);

        List<Integer> equipeIds = Arrays.asList(1, 2);

        when(tournoiService.affecterEquipesATournoi(eq(1), eq(equipeIds))).thenReturn(tournoi);

        ResponseEntity<?> response = tournoiController.affecterEquipesATournoi(1, equipeIds);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tournoi, response.getBody());
        verify(tournoiService, times(1)).affecterEquipesATournoi(1, equipeIds);
    }

    // Tu peux ajouter ici d'autres tests (ex : desaffecterEquipeDuTournoi, genererMatchs, etc.)
}
