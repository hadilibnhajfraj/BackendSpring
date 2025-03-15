package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String contenu;
    private LocalDate datePublication;
    private String typeMedia;
    private String urlMedia;

    @ManyToOne
    // @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private List<Commentaire> commentaires;
}
