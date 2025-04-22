package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Disponibilite_terrain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private DayOfWeek jour; //
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean disponible; //


    @JsonIgnore
    @ManyToOne
    // @JoinColumn(name = "id_terrain")
    private Terrain terrain;
}
