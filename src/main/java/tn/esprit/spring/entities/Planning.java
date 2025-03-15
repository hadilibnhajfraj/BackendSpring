package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Planning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idPlanning;

    @OneToOne
    //  @JoinColumn(name = "id_tournoi")
    Tournoi tournoi;

    @OneToMany(mappedBy = "planning", cascade = CascadeType.ALL)
    List<MatchFo> matchs;

}