package com.hotel.model;

public class Chambre {
    public String numChambre;
    public String design;
    public String type;
    public int prixNuite;

    public Chambre() {}

    public Chambre(String numChambre, String design, String type, int prixNuite) {
        this.numChambre = numChambre;
        this.design = design;
        this.type = type;
        this.prixNuite = prixNuite;
    }
}
