package com.hotel.dao;

import com.hotel.db.Database;
import com.hotel.model.Chambre;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChambreDao {

    public List<Chambre> findAll() throws SQLException {
        String sql = "SELECT * FROM chambre ORDER BY num_chambre";
        List<Chambre> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Chambre findById(String numChambre) throws SQLException {
        String sql = "SELECT * FROM chambre WHERE num_chambre = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numChambre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void create(Chambre ch) throws SQLException {
        String sql = "INSERT INTO chambre (num_chambre, design, type, prix_nuite) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ch.numChambre);
            ps.setString(2, ch.design);
            ps.setString(3, ch.type);
            ps.setInt(4, ch.prixNuite);
            ps.executeUpdate();
        }
    }

    public boolean update(String numChambre, Chambre ch) throws SQLException {
        String sql = "UPDATE chambre SET design=?, type=?, prix_nuite=? WHERE num_chambre=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ch.design);
            ps.setString(2, ch.type);
            ps.setInt(3, ch.prixNuite);
            ps.setString(4, numChambre);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String numChambre) throws SQLException {
        String sql = "DELETE FROM chambre WHERE num_chambre=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numChambre);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Recherche des chambres libres pour une date d'entrée et un nombre de jours donnés.
     * Une chambre est indisponible si elle a une réservation ACTIVE ou un séjour
     * dont l'intervalle [debut, debut+nbrJour) chevauche l'intervalle demandé.
     */
    public List<Chambre> findAvailable(LocalDate dateEntree, int nbrJour) throws SQLException {
        String sql = """
            SELECT * FROM chambre c
            WHERE c.num_chambre NOT IN (
                SELECT r.num_chambre FROM reserver r
                WHERE r.statut IN ('ACTIVE','OCCUPEE')
                  AND r.date_entree < DATE_ADD(?, INTERVAL ? DAY)
                  AND DATE_ADD(r.date_entree, INTERVAL r.nbr_jour DAY) > ?
            )
            AND c.num_chambre NOT IN (
                SELECT s.num_chambre FROM sejourner s
                WHERE s.date_entree_sejour < DATE_ADD(?, INTERVAL ? DAY)
                  AND DATE_ADD(s.date_entree_sejour, INTERVAL s.nbr_jour DAY) > ?
            )
            ORDER BY c.num_chambre
            """;
        List<Chambre> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateEntree));
            ps.setInt(2, nbrJour);
            ps.setDate(3, Date.valueOf(dateEntree));
            ps.setDate(4, Date.valueOf(dateEntree));
            ps.setInt(5, nbrJour);
            ps.setDate(6, Date.valueOf(dateEntree));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Chambre map(ResultSet rs) throws SQLException {
        return new Chambre(
            rs.getString("num_chambre"),
            rs.getString("design"),
            rs.getString("type"),
            rs.getInt("prix_nuite")
        );
    }
}
