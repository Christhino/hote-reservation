package com.hotel.dao;

import com.hotel.db.Database;
import com.hotel.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    public List<Reservation> findAll() throws SQLException {
        String sql = "SELECT * FROM reserver ORDER BY id_reserv DESC";
        List<Reservation> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Reservation findById(int id) throws SQLException {
        String sql = "SELECT * FROM reserver WHERE id_reserv=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Vérifie qu'aucune réservation ACTIVE/OCCUPEE existante ne chevauche
     * la période demandée pour la chambre concernée.
     * (Règle : une chambre réservée à une date ne peut plus être prise par un autre client)
     */
    public boolean isAvailable(String numChambre, LocalDate dateEntree, int nbrJour, Integer excludeId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM reserver
            WHERE num_chambre = ?
              AND statut IN ('ACTIVE','OCCUPEE')
              AND date_entree < DATE_ADD(?, INTERVAL ? DAY)
              AND DATE_ADD(date_entree, INTERVAL nbr_jour DAY) > ?
              AND (? IS NULL OR id_reserv <> ?)
            """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numChambre);
            ps.setDate(2, Date.valueOf(dateEntree));
            ps.setInt(3, nbrJour);
            ps.setDate(4, Date.valueOf(dateEntree));
            if (excludeId == null) {
                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(5, excludeId);
                ps.setInt(6, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    /** Crée une réservation. La date de réservation est fixée à la date du jour. */
    public int create(Reservation r) throws SQLException {
        String sql = "INSERT INTO reserver (num_chambre, date_reserv, date_entree, nbr_jour, nom_client, mail, statut) " +
                     "VALUES (?, CURDATE(), ?, ?, ?, ?, 'ACTIVE')";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.numChambre);
            ps.setDate(2, Date.valueOf(r.dateEntree));
            ps.setInt(3, r.nbrJour);
            ps.setString(4, r.nomClient);
            ps.setString(5, r.mail);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public boolean update(int id, Reservation r) throws SQLException {
        String sql = "UPDATE reserver SET num_chambre=?, date_entree=?, nbr_jour=?, nom_client=?, mail=? WHERE id_reserv=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.numChambre);
            ps.setDate(2, Date.valueOf(r.dateEntree));
            ps.setInt(3, r.nbrJour);
            ps.setString(4, r.nomClient);
            ps.setString(5, r.mail);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Annulation : la ou les chambres redeviennent libres (statut = ANNULEE). */
    public boolean cancel(int id) throws SQLException {
        String sql = "UPDATE reserver SET statut='ANNULEE' WHERE id_reserv=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markOccupee(int id) throws SQLException {
        String sql = "UPDATE reserver SET statut='OCCUPEE' WHERE id_reserv=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM reserver WHERE id_reserv=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.idReserv = rs.getInt("id_reserv");
        r.numChambre = rs.getString("num_chambre");
        r.dateReserv = rs.getDate("date_reserv").toLocalDate();
        r.dateEntree = rs.getDate("date_entree").toLocalDate();
        r.nbrJour = rs.getInt("nbr_jour");
        r.nomClient = rs.getString("nom_client");
        r.mail = rs.getString("mail");
        r.statut = rs.getString("statut");
        return r;
    }
}
