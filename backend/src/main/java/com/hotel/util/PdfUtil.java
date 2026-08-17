package com.hotel.util;

import com.hotel.model.Sejour;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class PdfUtil {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Génère le reçu PDF d'un séjour, au format demandé :
     * Sejour N° ...
     * Nom du Client : ...
     * Désignation chambre : ...
     * Nombre de jour : ... jours
     * Date d'entrée : ...
     * Date de sortie : ...
     */
    public static byte[] genererRecuSejour(Sejour s, String designChambre) throws DocumentException {
        Document document = new Document(PageSize.A5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);

        Paragraph p0 = new Paragraph("REÇU DE SÉJOUR", titre);
        p0.setAlignment(Element.ALIGN_CENTER);
        p0.setSpacingAfter(20);
        document.add(p0);

        var dateSortie = s.dateEntreeSejour.plusDays(s.nbrJour);

        document.add(new Paragraph("Sejour N° " + String.format("%03d", s.idSejour), normal));
        document.add(new Paragraph("Nom du Client : " + s.nomClient, normal));
        document.add(new Paragraph("Désignation chambre : " + designChambre, normal));
        document.add(new Paragraph("Nombre de jour : " + s.nbrJour + " jours", normal));
        document.add(new Paragraph("Date d'entrée : " + s.dateEntreeSejour.format(FMT), normal));
        document.add(new Paragraph("Date de sortie : " + dateSortie.format(FMT), normal));

        document.close();
        return out.toByteArray();
    }
}
