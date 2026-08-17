package com.hotel.model;

import java.time.LocalDate;

public class Sejour {
    public int idSejour;
    public String numChambre;
    public LocalDate dateEntreeSejour; // fixée automatiquement à la date du jour
    public int nbrJour;
    public String nomClient;
    public String telephone;

    public Sejour() {}
}
