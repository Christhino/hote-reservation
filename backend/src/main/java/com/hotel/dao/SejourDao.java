package com.hotel.dao;

import com.hotel.db.Database;
import com.hotel.model.Sejour;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SejourDao {

    public List<Sejour> findAll() throws SQLException {
        String sql = "SELECT * FROM sejourner ORDER BY id_sejour DESC";
        List<Sejour> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Sejour findById(int id) throws SQLException {
        String sql = "SELECT * FROM sejourner WHERE id_sejour=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Crée un séjour. La date d'entrée est fixée automatiquement à la date du jour. */
    public int create(Sejour s) throws SQLException {
        String sql = "INSERT INTO sejourner (num_chambre, date_entree_sejour, nbr_jour, nom_client, telephone) " +
                     "VALUES (?, CURDATE(), ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.numChambre);
            ps.setInt(2, s.nbrJour);
            ps.setString(3, s.nomClient);
            ps.setString(4, s.telephone);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public boolean update(int id, Sejour s) throws SQLException {
        String sql = "UPDATE sejourner SET num_chambre=?, nbr_jour=?, nom_client=?, telephone=? WHERE id_sejour=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.numChambre);
            ps.setInt(2, s.nbrJour);
            ps.setString(3, s.nomClient);
            ps.setString(4, s.telephone);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM sejourner WHERE id_sejour=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getPrixNuite(String numChambre) throws SQLException {
        String sql = "SELECT prix_nuite FROM chambre WHERE num_chambre=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numChambre);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Sejour map(ResultSet rs) throws SQLException {
        Sejour s = new Sejour();
        s.idSejour = rs.getInt("id_sejour");
        s.numChambre = rs.getString("num_chambre");
        s.dateEntreeSejour = rs.getDate("date_entree_sejour").toLocalDate();
        s.nbrJour = rs.getInt("nbr_jour");
        s.nomClient = rs.getString("nom_client");
        s.telephone = rs.getString("telephone");
        return s;
    }
}
