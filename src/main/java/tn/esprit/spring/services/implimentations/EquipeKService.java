package tn.esprit.spring.services.implimentations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.services.interfaces.IEquipeService;

@Service
public class EquipeKService implements IEquipeService {

    @Autowired
    EquipeRepository equipeRepository;

    // Implémentation de la méthode pour ajouter une équipe
    @Override
    public Equipe addEquipe(Equipe equipe) {
        return equipeRepository.save(equipe);
    }


}
