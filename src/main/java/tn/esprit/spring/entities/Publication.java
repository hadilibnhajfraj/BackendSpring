package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String contenu;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate datePublication;
    private String typeMedia;
    private String urlMedia;
    private boolean isLive; // Nouveau champ pour identifier un direct
    private int nombreReactions = 0;
    @Override
    public String toString() {
        return "Publication{id=" + id + ", contenu='" + contenu + "', datePublication=" + datePublication + ", isLive=" + isLive + ", typeMedia=" + typeMedia + ", urlMedia='" + urlMedia + "'}";
    }
    public boolean isLive() {
        return isLive;
    }

    public void setIsLive(boolean live) {
        this.isLive = live;
    }
    @ManyToOne

    // @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Commentaire> commentaires;
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ReactionPublication> reactions;
}
