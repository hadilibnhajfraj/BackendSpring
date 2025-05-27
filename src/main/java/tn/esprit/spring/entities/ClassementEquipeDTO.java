package tn.esprit.spring.entities;

public class ClassementEquipeDTO {

    private String nomEquipe;
    public int points = 0;
    public int matchsJoues = 0;
    public int butsMarques = 0;
    public int butsEncaisses = 0;
    public int differenceButs = 0;

    private String logo;


    // ✅ Nouveaux champs
    public int cartonsJaunes = 0;
    public int cartonsRouges = 0;
    public int corners = 0;

    public ClassementEquipeDTO(String nomEquipe, String logo) {
        this.nomEquipe = nomEquipe;
        this.logo = logo;
    }


    public void calculerDifferenceButs() {
        this.differenceButs = this.butsMarques - this.butsEncaisses;
    }

    // Getters
    public String getNomEquipe() { return nomEquipe; }
    public int getPoints() { return points; }
    public int getMatchsJoues() { return matchsJoues; }
    public int getButsMarques() { return butsMarques; }
    public int getButsEncaisses() { return butsEncaisses; }
    public int getDifferenceButs() { return differenceButs; }

    // ✅ Nouveaux getters
    public int getCartonsJaunes() { return cartonsJaunes; }
    public int getCartonsRouges() { return cartonsRouges; }
    public int getCorners() { return corners; }
    public String getLogo() {
        return logo;
    }


    @Override
    public String toString() {
        return nomEquipe + " | Points: " + points +
                " | MJ: " + matchsJoues +
                " | BM: " + butsMarques +
                " | BE: " + butsEncaisses +
                " | Diff: " + differenceButs +
                " | CJ: " + cartonsJaunes +
                " | CR: " + cartonsRouges +
                " | Corners: " + corners;
    }
}
