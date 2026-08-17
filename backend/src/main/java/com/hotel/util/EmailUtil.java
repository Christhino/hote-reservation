package com.hotel.util;

import com.hotel.Config;
import com.hotel.model.Reservation;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailUtil {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Envoie automatiquement un email de confirmation au client après une réservation,
     * avec la chambre occupée, les dates et le nombre de jours.
     * L'envoi est fait de façon asynchrone pour ne pas bloquer la réponse HTTP,
     * et il est ignoré silencieusement si le SMTP n'est pas configuré (mode dev).
     */
    public static void envoyerConfirmationReservation(Reservation r) {
        if (!Config.SMTP_ENABLED) {
            System.out.println("[EMAIL] SMTP non configuré : email non envoyé (mode démo). Destinataire=" + r.mail);
            return;
        }

        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", Config.SMTP_HOST);
                props.put("mail.smtp.port", String.valueOf(Config.SMTP_PORT));

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.SMTP_USER, Config.SMTP_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Config.SMTP_USER));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(r.mail));
                message.setSubject("Confirmation de votre réservation - Chambre " + r.numChambre);

                var dateSortie = r.dateEntree.plusDays(r.nbrJour);
                String corps = """
                    Bonjour %s,

                    Votre réservation a bien été enregistrée. Voici le récapitulatif :

                    Chambre réservée : %s
                    Date de réservation : %s
                    Date d'entrée : %s
                    Date de sortie : %s
                    Nombre de jour(s) : %d

                    Merci de votre confiance.
                    L'équipe de l'hôtel
                    """.formatted(
                        r.nomClient, r.numChambre,
                        r.dateReserv.format(FMT), r.dateEntree.format(FMT),
                        dateSortie.format(FMT), r.nbrJour
                );

                message.setText(corps);
                Transport.send(message);
                System.out.println("[EMAIL] Confirmation envoyée à " + r.mail);
            } catch (Exception e) {
                System.err.println("[EMAIL] Échec de l'envoi : " + e.getMessage());
            }
        }).start();
    }
}
