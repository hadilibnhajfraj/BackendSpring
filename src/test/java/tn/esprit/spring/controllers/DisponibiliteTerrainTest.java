package tn.esprit.spring.controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.services.Iservice;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

class DisponibiliteTerrainControllerTest {

    @InjectMocks
    private DisponibiliteTerrainController disponibiliteTerrainController;

    @Mock
    private Iservice disponibiliteTerrainService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterDisponibilite() {
        Disponibilite_terrain input = new Disponibilite_terrain();
        input.setId(1);
        input.setDate(LocalDate.of(2025, 6, 27));
        input.setJour(DayOfWeek.FRIDAY);
        input.setHeureDebut(LocalTime.of(9, 0));
        input.setHeureFin(LocalTime.of(12, 0));
        input.setDisponible(true);

        when(disponibiliteTerrainService.ajouterDisponibilteTerrain(input)).thenReturn(input);

        ResponseEntity<Disponibilite_terrain> response = disponibiliteTerrainController.ajouterDisponibiliteTerrain(input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(disponibiliteTerrainService, times(1)).ajouterDisponibilteTerrain(input);
    }

    @Test
    void testUpdateDisponibilite() {
        int id = 1;
        Disponibilite_terrain input = new Disponibilite_terrain();
        input.setId(id);
        input.setDisponible(false);

        when(disponibiliteTerrainService.updateDisponibiliteTerrain(id, input)).thenReturn(input);

        ResponseEntity<Disponibilite_terrain> response = disponibiliteTerrainController.updateDisponibiliteTerrain(id, input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(disponibiliteTerrainService, times(1)).updateDisponibiliteTerrain(id, input);
    }

    @Test
    void testGetAllDisponibilites() {
        Disponibilite_terrain d1 = new Disponibilite_terrain();
        d1.setId(1);
        Disponibilite_terrain d2 = new Disponibilite_terrain();
        d2.setId(2);

        List<Disponibilite_terrain> list = Arrays.asList(d1, d2);

        when(disponibiliteTerrainService.getAllDisponibiliteTerrain()).thenReturn(list);

        ResponseEntity<List<Disponibilite_terrain>> response = disponibiliteTerrainController.getAllDisponibilites();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).hasSize(2);

        verify(disponibiliteTerrainService, times(1)).getAllDisponibiliteTerrain();
    }

    @Test
    void testGetDisponibiliteById_Found() {
        int id = 1;
        Disponibilite_terrain dispo = new Disponibilite_terrain();
        dispo.setId(id);

        when(disponibiliteTerrainService.getDisponibiliteTerrainById(id)).thenReturn(Optional.of(dispo));

        ResponseEntity<Disponibilite_terrain> response = disponibiliteTerrainController.getDisponibiliteById(id);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(dispo);

        verify(disponibiliteTerrainService, times(1)).getDisponibiliteTerrainById(id);
    }

    @Test
    void testGetDisponibiliteById_NotFound() {
        int id = 1;

        when(disponibiliteTerrainService.getDisponibiliteTerrainById(id)).thenReturn(Optional.empty());

        ResponseEntity<Disponibilite_terrain> response = disponibiliteTerrainController.getDisponibiliteById(id);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(disponibiliteTerrainService, times(1)).getDisponibiliteTerrainById(id);
    }

    @Test
    void testDeleteDisponibilite() {
        int id = 1;

        doNothing().when(disponibiliteTerrainService).deleteDisponibiliteTerrain(id);

        ResponseEntity<Void> response = disponibiliteTerrainController.deleteDisponibilite(id);

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(disponibiliteTerrainService, times(1)).deleteDisponibiliteTerrain(id);
    }
}
