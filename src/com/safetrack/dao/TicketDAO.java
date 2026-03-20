package com.safetrack.dao;

import com.safetrack.model.Ticket;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class TicketDAO {

    public Ticket bookTicket(int userId, int busId, int seat) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String check = "SELECT * FROM tickets WHERE seat=?";
            PreparedStatement ps1 = conn.prepareStatement(check);
            ps1.setInt(1, seat);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) return null;

            String sql = "INSERT INTO tickets(userId, busId, seat) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, busId);
            ps.setInt(3, seat);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return new Ticket(keys.getInt(1), userId, busId, seat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ticket> getTicketsByUser(int userId) {
        List<Ticket> list = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM tickets WHERE userId=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Ticket(
                        rs.getInt("id"),
                        userId,
                        rs.getInt("busId"),
                        rs.getInt("seat")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void cancelTicket(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM tickets WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}