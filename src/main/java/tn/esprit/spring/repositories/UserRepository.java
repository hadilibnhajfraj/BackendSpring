package tn.esprit.spring.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.User;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
