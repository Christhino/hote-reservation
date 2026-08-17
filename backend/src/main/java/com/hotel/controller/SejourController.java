package com.hotel.controller;

import com.hotel.dao.ChambreDao;
import com.hotel.dao.SejourDao;
import com.hotel.dao.SoldeDao;
import com.hotel.model.Chambre;
import com.hotel.model.Sejour;
import com.hotel.util.PdfUtil;
import io.javalin.Javalin;
import io.javalin.http.ContentType;

import java.util.Map;

public class SejourController {

    private final SejourDao dao = new SejourDao();
    private final ChambreDao chambreDao = new ChambreDao();
    private final SoldeDao soldeDao = new SoldeDao();

    public void register(Javalin app) {
        app.get("/api/sejours", ctx -> ctx.json(dao.findAll()));

        app.get("/api/sejours/{id}", ctx -> {
            Sejour s = dao.findById(Integer.parseInt(ctx.pathParam("id")));
            if (s == null) { ctx.status(404).json(Map.of("error", "Séjour introuvable")); return; }
            ctx.json(s);
        });

        // Création d'un séjour (client sans réservation préalable). Le solde s'additionne.
        app.post("/api/sejours", ctx -> {
            Sejour s = ctx.bodyAsClass(Sejour.class);

            Chambre ch = chambreDao.findById(s.numChambre);
            if (ch == null) { ctx.status(404).json(Map.of("error", "Chambre introuvable")); return; }

            int id = dao.create(s);
            int montant = ch.prixNuite * s.nbrJour;
            soldeDao.addToSolde(montant);

            ctx.status(201).json(dao.findById(id));
        });

        app.put("/api/sejours/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Sejour s = ctx.bodyAsClass(Sejour.class);
            boolean ok = dao.update(id, s);
            if (!ok) { ctx.status(404).json(Map.of("error", "Séjour introuvable")); return; }
            ctx.json(dao.findById(id));
        });

        app.delete("/api/sejours/{id}", ctx -> {
            boolean ok = dao.delete(Integer.parseInt(ctx.pathParam("id")));
            if (!ok) { ctx.status(404).json(Map.of("error", "Séjour introuvable")); return; }
            ctx.status(204);
        });

        // Génération du reçu PDF
        app.get("/api/sejours/{id}/recu", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Sejour s = dao.findById(id);
            if (s == null) { ctx.status(404).json(Map.of("error", "Séjour introuvable")); return; }
            Chambre ch = chambreDao.findById(s.numChambre);

            byte[] pdf = PdfUtil.genererRecuSejour(s, ch != null ? ch.design : s.numChambre);
            ctx.contentType(ContentType.APPLICATION_PDF)
               .header("Content-Disposition", "inline; filename=recu_sejour_" + id + ".pdf")
               .result(pdf);
        });
    }
}
