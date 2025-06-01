package tn.esprit.spring.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int   id;
    public String nom;
    public String prenom;
    public String email;
    public String password;
    public Role role;
    private String displayName;


}
