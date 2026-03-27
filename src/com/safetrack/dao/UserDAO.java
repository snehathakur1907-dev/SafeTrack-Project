package com.safetrack.dao;

import com.safetrack.model.*;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;

/**
 * Data Access Object for User.
 * Handles login authentication and user lookup via JDBC.
 */
public class UserDAO {

    /**
     * Authenticates a user by email/username and password.
     * Returns int[2] = {id, roleCode} where roleCode 1=ADMIN, 2=PASSENGER.
     * Returns null if credentials are invalid.
     */
    public int[] loginAndGetIdRole(String emailOrUsername, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, role FROM users WHERE (email = ? OR username = ?) AND password = ?");
            ps.setString(1, emailOrUsername);
            ps.setString(2, emailOrUsername);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id   = rs.getInt("id");
                int role = "ADMIN".equalsIgnoreCase(rs.getString("role")) ? 1 : 2;
                return new int[]{id, role};
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Authenticates a user and returns the matching User model (Admin or Passenger).
     * Returns null if credentials are invalid.
     */
    public User login(String emailOrUsername, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE (email = ? OR username = ?) AND password = ?");
            ps.setString(1, emailOrUsername);
            ps.setString(2, emailOrUsername);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int    id   = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                if ("ADMIN".equalsIgnoreCase(role)) return new Admin(id, name, email, password);
                else return new Passenger(id, name, email, password);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Looks up a user by their primary key ID.
     * Returns null if no user is found.
     */
    public User findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name, email, role FROM users WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                String name = rs.getString("name");
                String email = rs.getString("email");
                if ("ADMIN".equalsIgnoreCase(role)) {
                    return new Admin(id, name, email, "");
                } else {
                    return new Passenger(id, name, email, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing user's profile.
     * Updates password only if it is not empty.
     */
    public boolean updateProfile(int userId, String name, String username, String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean updatePass = password != null && !password.isEmpty();
            String q = updatePass ? "UPDATE users SET name=?, username=?, email=?, password=? WHERE id=?" 
                                  : "UPDATE users SET name=?, username=?, email=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, email);
            if (updatePass) {
                ps.setString(4, password);
                ps.setInt(5, userId);
            } else {
                ps.setInt(4, userId);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets partial user details (name, username, email) specifically for the profile editor.
     * Returns a string array [name, username, email] or null if not found.
     */
    public String[] getUserProfileDetails(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name, username, email FROM users WHERE id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new String[]{rs.getString("name"), rs.getString("username"), rs.getString("email")};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches a list of all users, optionally limited by a number.
     * Returns a list of strings arrays where each array represents a row: [id, name, username, email, role].
     */
    public java.util.List<String[]> getAllUsers(int limit) {
        java.util.List<String[]> users = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, name, username, email, role FROM users ORDER BY id DESC" +
                    (limit > 0 ? " LIMIT " + limit : "");
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                users.add(new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Deletes a user record by ID.
     * @param id the primary key of the user to delete
     */
    public void deleteUser(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}