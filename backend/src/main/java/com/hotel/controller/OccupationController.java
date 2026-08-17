package com.hotel.controller;

import com.hotel.dao.ChambreDao;
import com.hotel.dao.OccupationDao;
import com.hotel.dao.ReservationDao;
import com.hotel.dao.SoldeDao;
import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import io.javalin.Javalin;

import java.util.Map;

public class OccupationController {

    private final OccupationDao dao = new OccupationDao();
    private final ReservationDao reservationDao = new ReservationDao();
    private final ChambreDao chambreDao = new ChambreDao();
    private final SoldeDao soldeDao = new SoldeDao();

    public void register(Javalin app) {
        app.get("/api/occupations", ctx -> ctx.json(dao.findAll()));

        // Arrivée d'un client qui avait réservé en avance : le solde s'additionne
        app.post("/api/occupations", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int idReserv = ((Number) body.get("idReserv")).intValue();

            Reservation r = reservationDao.findById(idReserv);
            if (r == null) { ctx.status(404).json(Map.of("error", "Réservation introuvable")); return; }
            if (!"ACTIVE".equals(r.statut)) {
                ctx.status(409).json(Map.of("error", "Cette réservation n'est plus active (statut=" + r.statut + ")"));
                return;
            }

            Chambre ch = chambreDao.findById(r.numChambre);
            int montant = ch.prixNuite * r.nbrJour;

            int idOccup = dao.create(idReserv);
            reservationDao.markOccupee(idReserv);
            soldeDao.addToSolde(montant);

            ctx.status(201).json(Map.of("idOccup", idOccup, "montant", montant));
        });

        app.delete("/api/occupations/{id}", ctx -> {
            boolean ok = dao.delete(Integer.parseInt(ctx.pathParam("id")));
            if (!ok) { ctx.status(404).json(Map.of("error", "Occupation introuvable")); return; }
            ctx.status(204);
        });

        app.get("/api/solde", ctx -> ctx.json(soldeDao.get()));
    }
}
