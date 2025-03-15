package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Billet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int prix;
    private LocalDate dateAchat;

    @ManyToOne
    //@JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    //@JoinColumn(name = "id_match")
    private MatchFo matchf;
}
