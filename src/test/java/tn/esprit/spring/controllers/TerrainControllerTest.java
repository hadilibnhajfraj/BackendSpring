package tn.esprit.spring.controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.services.Iservice;

import java.util.*;

class TerrainControllerTest {

    @InjectMocks
    private TerrainController terrainController;

    @Mock
    private Iservice iservice;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterTerrain() {
        Terrain input = new Terrain();
        input.setId(1);
        input.setNom("Terrain A");
        // autres propriétés si besoin

        when(iservice.ajouterTerrain(input)).thenReturn(input);

        ResponseEntity<Terrain> response = terrainController.ajouterTerrain(input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(iservice, times(1)).ajouterTerrain(input);
    }

    @Test
    void testUpdateTerrain() {
        int id = 1;
        Terrain input = new Terrain();
        input.setId(id);
        input.setNom("Terrain Updated");

        when(iservice.updateTerrain(id, input)).thenReturn(input);

        ResponseEntity<Terrain> response = terrainController.updateTerrain(id, input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(iservice, times(1)).updateTerrain(id, input);
    }

    @Test
    void testGetAllTerrains() {
        List<Terrain> terrains = new ArrayList<>();
        Terrain t1 = new Terrain();
        t1.setId(1);
        Terrain t2 = new Terrain();
        t2.setId(2);
        terrains.add(t1);
        terrains.add(t2);

        when(iservice.getAllTerrains()).thenReturn(terrains);

        ResponseEntity<List<Terrain>> response = terrainController.getAllTerrains();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).hasSize(2);

        verify(iservice, times(1)).getAllTerrains();
    }

    @Test
    void testGetTerrainById_Found() {
        int id = 1;
        Terrain terrain = new Terrain();
        terrain.setId(id);

        when(iservice.getTerrainById(id)).thenReturn(Optional.of(terrain));

        ResponseEntity<Terrain> response = terrainController.getTerrainById(id);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(terrain);

        verify(iservice, times(1)).getTerrainById(id);
    }

    @Test
    void testGetTerrainById_NotFound() {
        int id = 1;

        when(iservice.getTerrainById(id)).thenReturn(Optional.empty());

        ResponseEntity<Terrain> response = terrainController.getTerrainById(id);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(iservice, times(1)).getTerrainById(id);
    }



}
