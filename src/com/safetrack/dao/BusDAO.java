package com.safetrack.dao;

import com.safetrack.model.Bus;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Bus.
 * All data is read from the 'buses' table in MySQL via JDBC.
 */
public class BusDAO {

    /**
     * Returns all buses in the system.
     */
    public List<Bus> getBuses() {
        List<Bus> buses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT id, name, capacity, departure_time FROM buses ORDER BY id");
            while (rs.next()) {
                buses.add(new Bus(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("departure_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    /**
     * Returns all buses assigned to a specific route ID.
     */
    public List<Bus> getBusesByRoute(int routeId) {
        List<Bus> buses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name, capacity, departure_time FROM buses WHERE route_id = ? ORDER BY id");
            ps.setInt(1, routeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                buses.add(new Bus(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("departure_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    /**
     * Adds a new bus to the database.
     */
    public void addBus(String name, String number, int capacity, String time, int routeId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO buses (name, number, capacity, departure_time, route_id) VALUES (?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, number);
            ps.setInt(3, capacity);
            ps.setString(4, time);
            ps.setInt(5, routeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all buses with their route details for display in Admin tables.
     * Returns a list of string arrays: [id, name, number, capacity, routeString]
     */
    public List<String[]> getAllBusesWithRoutes() {
        List<String[]> busList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT b.*, r.source, r.destination FROM buses b LEFT JOIN routes r ON b.route_id = r.id ORDER BY b.id DESC");
            while (rs.next()) {
                String source = rs.getString("source");
                String dest = rs.getString("destination");
                String routeStr = (source != null && dest != null) ? (source + " \u2192 " + dest) : "Unassigned";
                
                busList.add(new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("number"),
                        rs.getString("capacity"),
                        rs.getString("departure_time"),
                        routeStr
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busList;
    }

    /**
     * Deletes a bus record by ID.
     * @param id the primary key of the bus to delete
     */
    public void deleteBus(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM buses WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}