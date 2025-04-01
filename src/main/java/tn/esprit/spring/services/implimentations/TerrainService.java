package tn.esprit.spring.services.implimentations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.repositories.TerrainRepository;
import tn.esprit.spring.services.interfaces.ITerrainService;

@Service
public class TerrainService implements ITerrainService {

    @Autowired
    TerrainRepository terrainRepository;


    @Override
    public Terrain addTerrain(Terrain terrain) {
        return terrainRepository.save(terrain);
    }
}
