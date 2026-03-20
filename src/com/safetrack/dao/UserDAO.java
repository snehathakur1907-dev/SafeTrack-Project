package com.safetrack.dao;

import com.safetrack.model.*;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;

public class UserDAO {

    public User login(String email, String password) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                if (role.equals("ADMIN")) {
                    return new Admin(rs.getInt("id"), "", email, password);
                } else {
                    return new Passenger(rs.getInt("id"), "", email, password);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
