package com.hotel.model;

import java.time.LocalDate;

public class Occupation {
    public int idOccup;
    public int idReserv;
    public LocalDate dateOccup;

    // Champs enrichis (jointure) pour l'affichage côté frontend
    public String numChambre;
    public String nomClient;
    public int nbrJour;
    public int montant;

    public Occupation() {}
}
