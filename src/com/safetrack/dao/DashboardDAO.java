package com.safetrack.dao;

import com.safetrack.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Data Access Object for Dashboard Statistics.
 */
public class DashboardDAO {

    /**
     * Gets total counts for dashboard display.
     * Returns array [userCount, busCount, bookingCount, routeCount]
     */
    public int[] getDashboardStats() {
        int userCount = 0, busCount = 0, bookingCount = 0, routeCount = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            userCount = getCount(conn, "SELECT COUNT(*) FROM users");
            bookingCount = getCount(conn, "SELECT COUNT(*) FROM bookings");
            try { busCount = getCount(conn, "SELECT COUNT(*) FROM buses"); } catch (Exception ignored) {}
            try { routeCount = getCount(conn, "SELECT COUNT(*) FROM routes"); } catch (Exception ignored) {}
        } catch (Exception ignored) {}

        return new int[]{userCount, busCount, bookingCount, routeCount};
    }

    private int getCount(Connection conn, String sql) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }
}
