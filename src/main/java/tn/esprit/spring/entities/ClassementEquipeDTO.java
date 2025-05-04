package tn.esprit.spring.entities;

public class ClassementEquipeDTO {

    private String nomEquipe;
    public int points = 0;
    public int matchsJoues = 0;
    public int butsMarques = 0;
    public int butsEncaisses = 0;

    public ClassementEquipeDTO(String nomEquipe) {
        this.nomEquipe = nomEquipe;
    }

    // Getters pour le tri
    public int getPoints() { return points; }
    public String getNomEquipe() { return nomEquipe; }

    @Override
    public String toString() {
        return nomEquipe + " | Points: " + points +
                " | MJ: " + matchsJoues +
                " | BM: " + butsMarques +
                " | BE: " + butsEncaisses;
    }
}
