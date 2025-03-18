package tn.esprit.spring.services.implementations;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.services.interfaces.EquipeInterface;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EquipeService implements EquipeInterface {


    private final EquipeRepository equipeRepository;

    @Override
    public Equipe ajouterEquipe(Equipe equipe) {
        return  equipeRepository.save(equipe);
    }

    @Override
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    @Override
    public Optional<Equipe> getEquipeById(Integer id) {
        return equipeRepository.findById(id);
    }

    @Override
    public Equipe updateEquipe(Integer id, Equipe equipe) {
        if (equipeRepository.existsById(id)) {
            equipe.setIdEquipe(id);  // Maintenir l'ID existant lors de la mise à jour
            return equipeRepository.save(equipe);  // Sauvegarder l'équipe mise à jour
        }
        return null;  // Retourner null si l'équipe n'existe pas
    }

    @Override
    public void deleteEquipe(Integer id) {

        if (equipeRepository.existsById(id)) {
            equipeRepository.deleteById(id);  // Supprimer l'équipe si elle existe
        }
    }

    @Override
    public Equipe ajouterJoueur(Integer id, Integer playerId) {
        return null;
    }

    @Override
    public Equipe retirerJoueur(Integer id, Integer playerId) {
        return null;
    }
}
