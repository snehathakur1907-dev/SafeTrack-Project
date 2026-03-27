package com.safetrack.dao;

import com.safetrack.model.Ticket;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Ticket (bookings).
 * Handles booking, retrieval, cancellation, and status updates via JDBC.
 * Booking uses a transaction to prevent race-condition double-booking.
 */
public class TicketDAO {

    /**
     * Books a seat for a user on a specific bus.
     * Wraps the seat-check and insert in a single database transaction
     * to prevent race conditions where two users book the same seat simultaneously.
     *
     * @param userId     the passenger's user ID
     * @param busId      the bus being booked
     * @param seat       seat number (1-based)
     * @param date       journey date string (yyyy-MM-dd)
     * @param time       departure time string (e.g. "10:00 AM")
     * @param payMethod  selected payment method
     * @param payStatus  initial payment status ("PAID" or "PENDING")
     * @return the created Ticket, or null if the seat is already taken
     */
    public Ticket bookTicket(int userId, int busId, int seat,
                             String date, String time,
                             String payMethod, String payStatus) {

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return null;

            // ── BEGIN TRANSACTION ────────────────────────────────────────────
            conn.setAutoCommit(false);

            // 1. Check seat availability (scoped to bus + date + time)
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM bookings WHERE bus_id=? AND journey_date=? AND journey_time=? AND seat=?");
            check.setInt(1, busId);
            check.setString(2, date);
            check.setString(3, time);
            check.setInt(4, seat);
            if (check.executeQuery().next()) {
                conn.rollback(); // seat taken — roll back and return null
                return null;
            }

            // 2. Insert the new booking
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, bus_id, seat, journey_date, journey_time, " +
                    "payment_method, payment_status, ride_status) VALUES (?,?,?,?,?,?,?,'UPCOMING')",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);    ps.setInt(2, busId);
            ps.setInt(3, seat);      ps.setString(4, date);
            ps.setString(5, time);   ps.setString(6, payMethod);
            ps.setString(7, payStatus);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                conn.commit(); // ── COMMIT TRANSACTION ──────────────────────
                return new Ticket(keys.getInt(1), userId, busId, seat,
                        date, time, payMethod, payStatus, "UPCOMING");
            }

            conn.rollback();

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
        return null;
    }

    /**
     * Returns all tickets booked by a specific user.
     * @param userId the passenger's user ID
     * @return list of Ticket objects
     */
    public List<Ticket> getTicketsByUser(int userId) {
        List<Ticket> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return list;
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM bookings WHERE user_id = ? ORDER BY id DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Ticket(
                        rs.getInt("id"), userId, rs.getInt("bus_id"),
                        rs.getInt("seat"), rs.getString("journey_date"),
                        rs.getString("journey_time"), rs.getString("payment_method"),
                        rs.getString("payment_status"), rs.getString("ride_status")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Cancels (deletes) a ticket booking by its ID.
     * @param id the booking primary key
     */
    public void cancelTicket(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Returns all bookings with joined bus and route details for the Admin Dashboard.
     * @return list of String[] arrays: [id, user_id, bus_id, bus_name, route, date, time, seat, pay_method, pay_status, ride_status]
     */
    public List<String[]> getAllBookings() {
        List<String[]> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return bookings;
            String sql = "SELECT tk.*, b.name AS bus_name, r.source, r.destination " +
                         "FROM bookings tk " +
                         "JOIN buses b ON tk.bus_id = b.id " +
                         "LEFT JOIN routes r ON b.route_id = r.id " +
                         "ORDER BY tk.id DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                String src  = rs.getString("source");
                String dest = rs.getString("destination");
                String routeStr = (src != null && dest != null) ? (src + " \u2192 " + dest) : "-";
                bookings.add(new String[]{
                        rs.getString("id"),         rs.getString("user_id"),
                        rs.getString("bus_id"),      rs.getString("bus_name"),
                        routeStr,                    rs.getString("journey_date"),
                        rs.getString("journey_time"),rs.getString("seat"),
                        rs.getString("payment_method"), rs.getString("payment_status"),
                        rs.getString("ride_status")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return bookings;
    }

    /**
     * Returns the set of already-booked seat numbers for a bus on a specific date and time.
     * Used to populate the visual seat map — booked seats appear in red.
     *
     * @param busId bus primary key
     * @param date  journey date string
     * @param time  journey time string
     * @return set of booked seat numbers
     */
    public Set<Integer> getBookedSeats(int busId, String date, String time) {
        Set<Integer> booked = new HashSet<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return booked;
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT seat FROM bookings WHERE bus_id=? AND journey_date=? AND journey_time=?");
            ps.setInt(1, busId); ps.setString(2, date); ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) booked.add(rs.getInt("seat"));
        } catch (Exception e) { e.printStackTrace(); }
        return booked;
    }

    /**
     * Updates the ride status of a booking (e.g. UPCOMING → COMPLETED).
     * @param ticketId the booking primary key
     * @param status   new status string ("UPCOMING", "COMPLETED", or "CANCELLED")
     */
    public void updateRideStatus(int ticketId, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET ride_status = ? WHERE id = ?");
            ps.setString(1, status);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}