package com.hotel;

import com.hotel.controller.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Main {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper, true));
            // Autoriser le frontend React (CORS) - développement local
            config.bundledPlugins.enableCors(cors -> cors.addRule(rule -> rule.anyHost()));
        }).start(Config.SERVER_PORT);

        app.get("/", ctx -> ctx.result("API Gestion des réservations d'hôtel - OK"));

        new ChambreController().register(app);
        new ReservationController().register(app);
        new OccupationController().register(app);
        new SejourController().register(app);

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500).json(java.util.Map.of("error", e.getMessage()));
        });

        System.out.println("Serveur démarré sur http://localhost:" + Config.SERVER_PORT);
    }
}
