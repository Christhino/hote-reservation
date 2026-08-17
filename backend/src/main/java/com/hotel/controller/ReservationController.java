package com.hotel.controller;

import com.hotel.dao.ReservationDao;
import com.hotel.model.Reservation;
import com.hotel.util.EmailUtil;
import io.javalin.Javalin;

import java.util.Map;

public class ReservationController {

    private final ReservationDao dao = new ReservationDao();

    public void register(Javalin app) {
        app.get("/api/reservations", ctx -> ctx.json(dao.findAll()));

        app.get("/api/reservations/{id}", ctx -> {
            Reservation r = dao.findById(Integer.parseInt(ctx.pathParam("id")));
            if (r == null) { ctx.status(404).json(Map.of("error", "Réservation introuvable")); return; }
            ctx.json(r);
        });

        // Création d'une réservation
        app.post("/api/reservations", ctx -> {
            Reservation r = ctx.bodyAsClass(Reservation.class);

            // Règle : une chambre réservée à une date ne peut plus être prise par un autre client
            boolean libre = dao.isAvailable(r.numChambre, r.dateEntree, r.nbrJour, null);
            if (!libre) {
                ctx.status(409).json(Map.of("error",
                    "La chambre " + r.numChambre + " n'est pas disponible sur cette période."));
                return;
            }

            int id = dao.create(r);
            Reservation created = dao.findById(id);

            // Envoi automatique d'un email de confirmation au client
            EmailUtil.envoyerConfirmationReservation(created);

            ctx.status(201).json(created);
        });

        app.put("/api/reservations/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Reservation r = ctx.bodyAsClass(Reservation.class);

            boolean libre = dao.isAvailable(r.numChambre, r.dateEntree, r.nbrJour, id);
            if (!libre) {
                ctx.status(409).json(Map.of("error", "La chambre n'est pas disponible sur cette période."));
                return;
            }

            boolean ok = dao.update(id, r);
            if (!ok) { ctx.status(404).json(Map.of("error", "Réservation introuvable")); return; }
            ctx.json(dao.findById(id));
        });

        // Annulation : la chambre redevient libre
        app.put("/api/reservations/{id}/annuler", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean ok = dao.cancel(id);
            if (!ok) { ctx.status(404).json(Map.of("error", "Réservation introuvable")); return; }
            ctx.json(dao.findById(id));
        });

        app.delete("/api/reservations/{id}", ctx -> {
            boolean ok = dao.delete(Integer.parseInt(ctx.pathParam("id")));
            if (!ok) { ctx.status(404).json(Map.of("error", "Réservation introuvable")); return; }
            ctx.status(204);
        });
    }
}
