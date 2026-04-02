package com.safetrack.dao;

import com.safetrack.model.Route;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Route.
 * All route-related SQL lives here — views must not access the DB directly.
 */
public class RouteDAO {

    /**
     * Adds a new route to the database.
     * @param source      origin city/location
     * @param destination destination city/location
     * @param fare        ticket price in Rs.
     */
    public void addRoute(String source, String destination, double fare) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO routes (source, destination, fare) VALUES (?,?,?)");
            ps.setString(1, source);
            ps.setString(2, destination);
            ps.setDouble(3, fare);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Returns all routes as string arrays for admin table display.
     * @return list of [id, source, destination, fare] arrays
     */
    public List<String[]> getAllRoutes() {
        List<String[]> routes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return routes;
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM routes ORDER BY id DESC");
            while (rs.next()) {
                routes.add(new String[]{
                        rs.getString("id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("fare")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return routes;
    }

    /**
     * Returns all routes as typed Route model objects.
     * @return list of Route objects
     */
    public List<Route> getRouteList() {
        List<Route> routes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return routes;
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT source, destination FROM routes ORDER BY id");
            while (rs.next()) {
                routes.add(new Route(rs.getString("source"), rs.getString("destination")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return routes;
    }

    /**
     * Deletes a route record by its primary key.
     * @param id the route ID to delete
     */
    public void deleteRoute(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM routes WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
