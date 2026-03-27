package com.safetrack.dao;

import com.safetrack.model.EmergencyAlert;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for EmergencyAlert.
 * Saves emergency alerts to the 'alert_log' table in MySQL.
 */
public class EmergencyDAO {

    /**
     * Persists an emergency alert to the database.
     * Creates the alert_log table automatically if it does not exist.
     */
    public void saveAlert(EmergencyAlert alert) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Create table if first run
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS alert_log (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "user_id INT, " +
                            "message VARCHAR(255), " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO alert_log (user_id, message) VALUES (?, ?)");
            ps.setInt(1, alert.getUserId());
            ps.setString(2, alert.getMessage());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an emergency contact.
     */
    public void addEmergencyContact(String name, String phone, String relation) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO emergency_contacts (name, phone, relation) VALUES (?,?,?)");
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, relation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all emergency contacts.
     */
    public List<String[]> getAllEmergencyContacts() {
        List<String[]> contacts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM emergency_contacts ORDER BY id DESC");
            while (rs.next()) {
                contacts.add(new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("relation")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Deletes an emergency contact by ID.
     * @param id the primary key of the contact to delete
     */
    public void deleteContact(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM emergency_contacts WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}