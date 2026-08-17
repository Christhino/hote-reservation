package com.hotel.controller;

import com.hotel.dao.ChambreDao;
import com.hotel.model.Chambre;
import io.javalin.Javalin;

import java.time.LocalDate;
import java.util.Map;

public class ChambreController {

    private final ChambreDao dao = new ChambreDao();

    public void register(Javalin app) {
        app.get("/api/chambres", ctx -> ctx.json(dao.findAll()));

        app.get("/api/chambres/{id}", ctx -> {
            Chambre ch = dao.findById(ctx.pathParam("id"));
            if (ch == null) { ctx.status(404).json(Map.of("error", "Chambre introuvable")); return; }
            ctx.json(ch);
        });

        app.post("/api/chambres", ctx -> {
            Chambre ch = ctx.bodyAsClass(Chambre.class);
            dao.create(ch);
            ctx.status(201).json(ch);
        });

        app.put("/api/chambres/{id}", ctx -> {
            Chambre ch = ctx.bodyAsClass(Chambre.class);
            boolean ok = dao.update(ctx.pathParam("id"), ch);
            if (!ok) { ctx.status(404).json(Map.of("error", "Chambre introuvable")); return; }
            ctx.json(ch);
        });

        app.delete("/api/chambres/{id}", ctx -> {
            boolean ok = dao.delete(ctx.pathParam("id"));
            if (!ok) { ctx.status(404).json(Map.of("error", "Chambre introuvable")); return; }
            ctx.status(204);
        });

        // Recherche d'une chambre libre à une date donnée
        app.get("/api/chambres/disponibles", ctx -> {
            LocalDate dateEntree = LocalDate.parse(ctx.queryParam("dateEntree"));
            String nbrJourParam = ctx.queryParam("nbrJour");
            int nbrJour = (nbrJourParam == null || nbrJourParam.isBlank()) ? 1 : Integer.parseInt(nbrJourParam);
            ctx.json(dao.findAvailable(dateEntree, nbrJour));
        });
    }
}
