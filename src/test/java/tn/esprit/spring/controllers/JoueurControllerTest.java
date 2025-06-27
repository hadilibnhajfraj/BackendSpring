package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Joueur;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.services.implementations.JoueurService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JoueurController.class)
public class JoueurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JoueurService joueurService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JoueurService joueurService() {
            return mock(JoueurService.class);
        }
    }

    private Joueur joueur;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setNom("Test");
        user.setPrenom("Player");
        user.setRole(Role.Joueur);

        joueur = new Joueur();
        joueur.setIdJoueur(1);
        joueur.setNom("Test");
        joueur.setPrenom("Player");
        joueur.setUser(user);
        joueur.setTaille(180);
        joueur.setPoids(75);
        joueur.setPiedFort("Droit");
        joueur.setPoste("Attaquant");
        joueur.setDescription("Très rapide");
        joueur.setMail("test@example.com");
        joueur.setTel(12345678L);
    }

    @Test
    void testAjouterJoueur() throws Exception {
        when(joueurService.addJoueur(any(Joueur.class))).thenReturn(joueur);

        mockMvc.perform(post("/joueurs/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joueur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Test"))
                .andExpect(jsonPath("$.prenom").value("Player"));
    }

    @Test
    void testGetAllJoueurs() throws Exception {
        List<Joueur> joueurs = Arrays.asList(joueur);
        when(joueurService.getAllJoueurs()).thenReturn(joueurs);

        mockMvc.perform(get("/joueurs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Test"));
    }

    @Test
    void testGetJoueurById() throws Exception {
        when(joueurService.getJoueurById(1)).thenReturn(joueur);

        mockMvc.perform(get("/joueurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Test"))
                .andExpect(jsonPath("$.prenom").value("Player"));
    }

    @Test
    void testDeleteJoueur() throws Exception {
        doNothing().when(joueurService).deleteJoueur(1);

        mockMvc.perform(delete("/joueurs/delete/1"))
                .andExpect(status().isNoContent());
    }
}
