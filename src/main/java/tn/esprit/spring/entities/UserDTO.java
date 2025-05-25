package tn.esprit.spring.entities;

import java.time.LocalDate;

public class UserDTO {
    public String nom;
    public String prenom;
    public String email;
    public String password;
    public LocalDate dateNaissance;
    public Role role;
}