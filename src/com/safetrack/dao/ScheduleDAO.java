package com.safetrack.dao;

import com.safetrack.model.Schedule;
import com.safetrack.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Schedule.
 * Reads actual bus departure times from the database.
 * Previously returned hardcoded values — now uses real data.
 */
public class ScheduleDAO {

    /**
     * Returns a schedule entry for every bus, reading actual times from the database.
     * @return list of Schedule objects with real departure times from the buses table
     */
    public List<Schedule> getSchedules() {
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return list;
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT id, departure_time FROM buses ORDER BY id");
            while (rs.next()) {
                String depTime = rs.getString("departure_time");
                if (depTime == null || depTime.isEmpty()) depTime = "N/A";
                // Arrival time column not stored yet — placeholder used
                list.add(new Schedule(rs.getInt("id"), depTime, "N/A"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}