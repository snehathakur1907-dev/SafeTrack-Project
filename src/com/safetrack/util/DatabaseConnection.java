package com.safetrack.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Utility class that provides a MySQL database connection.
 * All DAO classes use this single point of connection.
 */
public class DatabaseConnection {

    private static final String URL  =
            "jdbc:mysql://localhost:3306/safetrack?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";

    /**
     * Opens and returns a new JDBC connection to the safetrack database.
     * Returns null if the connection fails.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}