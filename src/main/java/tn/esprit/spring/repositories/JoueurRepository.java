package tn.esprit.spring.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.Joueur;
import tn.esprit.spring.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Integer> {
    List<Joueur> findByEquipe(Equipe equipe);
    @Query("SELECT j FROM Joueur j WHERE j.equipe.idEquipe = :equipeId")
    List<Joueur> findByEquipeId(@Param("equipeId") Integer equipeId);
    @Query("SELECT j FROM Joueur j LEFT JOIN FETCH j.user WHERE j.idJoueur = :id")
    Optional<Joueur> findByIdWithUser(@Param("id") Integer id);


    // // Custom query to find all players who are not assigned to any team
   @Query("SELECT j FROM Joueur j WHERE j.equipe IS NULL")
   List<Joueur> findAllNonAssignedJoueurs();



}
