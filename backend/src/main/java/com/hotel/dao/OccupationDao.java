package com.hotel.dao;

import com.hotel.db.Database;
import com.hotel.model.Occupation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccupationDao {

    public List<Occupation> findAll() throws SQLException {
        String sql = """
            SELECT o.id_occup, o.id_reserv, o.date_occup,
                   r.num_chambre, r.nom_client, r.nbr_jour, c.prix_nuite
            FROM occuper o
            JOIN reserver r ON r.id_reserv = o.id_reserv
            JOIN chambre c ON c.num_chambre = r.num_chambre
            ORDER BY o.id_occup DESC
            """;
        List<Occupation> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM occuper WHERE id_occup=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int create(int idReserv) throws SQLException {
        String sql = "INSERT INTO occuper (id_reserv, date_occup) VALUES (?, CURDATE())";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idReserv);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private Occupation map(ResultSet rs) throws SQLException {
        Occupation o = new Occupation();
        o.idOccup = rs.getInt("id_occup");
        o.idReserv = rs.getInt("id_reserv");
        o.dateOccup = rs.getDate("date_occup").toLocalDate();
        o.numChambre = rs.getString("num_chambre");
        o.nomClient = rs.getString("nom_client");
        o.nbrJour = rs.getInt("nbr_jour");
        o.montant = o.nbrJour * rs.getInt("prix_nuite");
        return o;
    }
}
