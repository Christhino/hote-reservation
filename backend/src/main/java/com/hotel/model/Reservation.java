package com.hotel.model;

import java.time.LocalDate;

public class Reservation {
    public int idReserv;
    public String numChambre;
    public LocalDate dateReserv;   // fixée automatiquement à la date du jour
    public LocalDate dateEntree;
    public int nbrJour;
    public String nomClient;
    public String mail;
    public String statut; // ACTIVE, ANNULEE, OCCUPEE

    public Reservation() {}
}
