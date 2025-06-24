package tn.esprit.spring.controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.services.Iservice;

import java.util.*;

class MatchControllerTest {

    @InjectMocks
    private MatchController matchController;

    @Mock
    private Iservice iservice;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAjouterMatch() {
        MatchFo input = new MatchFo();
        input.setIdMatch(1);
        // configurer d'autres propriétés si besoin

        when(iservice.ajouterMatch(input)).thenReturn(input);

        ResponseEntity<MatchFo> response = matchController.ajouterMatch(input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(iservice, times(1)).ajouterMatch(input);
    }

    @Test
    void testUpdateMatch() {
        int id = 1;
        MatchFo input = new MatchFo();
        input.setIdMatch(id);

        when(iservice.updateMatch(id, input)).thenReturn(input);

        ResponseEntity<MatchFo> response = matchController.updateMatch(id, input);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(input);

        verify(iservice, times(1)).updateMatch(id, input);
    }

    @Test
    void testGetAllMatchs() {
        List<MatchFo> matchs = new ArrayList<>();
        MatchFo m1 = new MatchFo();
        m1.setIdMatch(1);
        MatchFo m2 = new MatchFo();
        m2.setIdMatch(2);
        matchs.add(m1);
        matchs.add(m2);

        when(iservice.getAllMatch()).thenReturn(matchs);

        ResponseEntity<List<MatchFo>> response = matchController.getAllMatchs();

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).hasSize(2);

        verify(iservice, times(1)).getAllMatch();
    }

    @Test
    void testGetMatchById_Found() {
        int id = 1;
        MatchFo match = new MatchFo();
        match.setIdMatch(id);

        when(iservice.getMatchId(id)).thenReturn(Optional.of(match));

        ResponseEntity<MatchFo> response = matchController.getMatchById(id);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(match);

        verify(iservice, times(1)).getMatchId(id);
    }

    @Test
    void testGetMatchById_NotFound() {
        int id = 1;

        when(iservice.getMatchId(id)).thenReturn(Optional.empty());

        ResponseEntity<MatchFo> response = matchController.getMatchById(id);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(iservice, times(1)).getMatchId(id);
    }

    @Test
    void testDeleteMatch() {
        int id = 1;

        doNothing().when(iservice).deleteMatch(id);

        ResponseEntity<Void> response = matchController.deleteMatch(id);

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(iservice, times(1)).deleteMatch(id);
    }
}
