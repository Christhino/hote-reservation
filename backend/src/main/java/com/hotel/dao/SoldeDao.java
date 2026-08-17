package com.hotel.dao;

import com.hotel.db.Database;
import com.hotel.model.Solde;

import java.sql.*;

public class SoldeDao {

    /** Récupère la ligne de solde unique (créée par le script SQL). */
    public Solde get() throws SQLException {
        String sql = "SELECT * FROM solde ORDER BY id LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Solde(rs.getInt("id"), rs.getInt("solde_actuel"));
            }
            // Aucune ligne : on en crée une
            return createInitial();
        }
    }

    private Solde createInitial() throws SQLException {
        String sql = "INSERT INTO solde (solde_actuel) VALUES (0)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Solde(keys.getInt(1), 0);
            }
        }
    }

    /** Additionne un montant au solde courant (arrivée d'un client). */
    public void addToSolde(int montant) throws SQLException {
        Solde s = get();
        String sql = "UPDATE solde SET solde_actuel = solde_actuel + ? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, montant);
            ps.setInt(2, s.id);
            ps.executeUpdate();
        }
    }
}
