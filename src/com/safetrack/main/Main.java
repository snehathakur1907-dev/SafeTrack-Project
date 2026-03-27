package com.safetrack.main;

import com.safetrack.util.DatabaseConnection;
import com.safetrack.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            new LoginView().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // IMPROVE RENDERING QUALITY (especially for Windows/Linux)
        // Force high-quality text anti-aliasing (LCD smoothing)
        System.setProperty("prism.lcdtext", "true");
        // Ensure UI scaling handles High-DPI screens correctly (Retina-like)
        System.setProperty("glass.win.uiScale", "1.0"); 
        // Use software rendering for shadows if hardware ones look "grainy" (optional)
        // System.setProperty("prism.forceGPU", "true");

        try {

            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = Objects.requireNonNull(conn).createStatement();

            // USERS TABLE (MySQL)
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(100), " +
                            "username VARCHAR(100) UNIQUE, " +
                            "email VARCHAR(100) UNIQUE, " +
                            "password VARCHAR(100), " +
                            "role VARCHAR(20))"
            );

            // BUSES TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS buses (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(100), " +
                            "number VARCHAR(50), " +
                            "capacity INT, " +
                            "route_id INT)"
            );
// Add 'number' column if it was missing from an earlier run
            try { stmt.execute("ALTER TABLE buses ADD COLUMN number VARCHAR(50)"); } catch (Exception ignored) {}
            // ADD TIMING TO BUSES
            try { stmt.execute("ALTER TABLE buses ADD COLUMN departure_time VARCHAR(20) DEFAULT '10:00 AM'"); } catch (Exception ignored) {}

            // ROUTES TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS routes (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "source VARCHAR(100), " +
                            "destination VARCHAR(100), " +
                            "fare DOUBLE)"
            );

            // BOOKINGS TABLE — with proper FOREIGN KEY constraints
            // Relationships:
            //   bookings.user_id (many) → users.id (one)
            //   bookings.bus_id  (many) → buses.id (one)
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "user_id INT, " +
                            "bus_id INT, " +
                            "seat INT, " +
                            "journey_date VARCHAR(20) DEFAULT '2026-03-30', " +
                            "journey_time VARCHAR(20) DEFAULT '10:00 AM', " +
                            "payment_method VARCHAR(50) DEFAULT 'Cash', " +
                            "payment_status VARCHAR(20) DEFAULT 'PENDING', " +
                            "ride_status VARCHAR(20) DEFAULT 'UPCOMING')"
            );

            // ADD FIELDS TO BOOKINGS (idempotent for existing deployments)
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN journey_date VARCHAR(20) DEFAULT '2026-03-30'"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN journey_time VARCHAR(20) DEFAULT '10:00 AM'"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN payment_method VARCHAR(50) DEFAULT 'Cash'"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN payment_status VARCHAR(20) DEFAULT 'PENDING'"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN ride_status VARCHAR(20) DEFAULT 'UPCOMING'"); } catch (Exception ignored) {}

            // ADD FOREIGN KEY CONSTRAINTS (try/catch for idempotency — MySQL ignores duplicate constraint names)
            // FK: bookings.user_id → users.id  (ON DELETE CASCADE — delete user removes their bookings)
            try { stmt.execute("ALTER TABLE bookings ADD CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"); } catch (Exception ignored) {}
            // FK: bookings.bus_id → buses.id   (ON DELETE CASCADE — delete bus removes its bookings)
            try { stmt.execute("ALTER TABLE bookings ADD CONSTRAINT fk_booking_bus FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE"); } catch (Exception ignored) {}
            // FK: buses.route_id → routes.id   (ON DELETE SET NULL — deleting route unassigns buses)
            try { stmt.execute("ALTER TABLE buses ADD CONSTRAINT fk_bus_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE SET NULL"); } catch (Exception ignored) {}


            // EMERGENCY CONTACTS TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS emergency_contacts (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(100), " +
                            "phone VARCHAR(50), " +
                            "relation VARCHAR(50))"
            );

            // DEFAULT EMERGENCY CONTACTS
            stmt.executeUpdate(
                    "INSERT IGNORE INTO emergency_contacts (id, name, phone, relation) VALUES " +
                            "(1, 'Police', '100', '🚒'), " +
                            "(2, 'Ambulance', '102', '🚑'), " +
                            "(3, 'Fire Brigade', '101', '🔥'), " +
                            "(4, 'SafeTrack Support', '9800000000', '🚌')"
            );

            // DEFAULT USERS (MySQL syntax)
            stmt.executeUpdate(
                    "INSERT IGNORE INTO users (id, name, username, email, password, role) VALUES " +
                            "(1, 'Admin', 'admin', 'admin@gmail.com', '123', 'ADMIN')"
            );

            stmt.executeUpdate(
                    "INSERT IGNORE INTO users (id, name, username, email, password, role) VALUES " +
                            "(2, 'User', 'user', 'user@gmail.com', '123', 'PASSENGER')"
            );

            // DEFAULT ROUTES
            stmt.executeUpdate(
                    "INSERT IGNORE INTO routes (id, source, destination, fare) VALUES " +
                            "(1, 'KTM', 'JKR', 1800), " +
                            "(2, 'PKR', 'KTM', 2000), " +
                            "(3, 'JKR', 'KTM', 2000), " +
                            "(4, 'KTM', 'PKR', 2100), " +
                            "(5, 'BIR', 'DHI', 2500), " +
                            "(6, 'DHI', 'BIR', 3000)"
            );

            // DEFAULT BUSES
            stmt.executeUpdate(
                    "INSERT IGNORE INTO buses (id, name, number, capacity, route_id) VALUES " +
                            "(1, 'BUS1', '101', 40, 1), " +
                            "(2, 'BUS2', '102', 40, 2), " +
                            "(3, 'BUS3', '103', 40, 3), " +
                            "(4, 'BUS4', '104', 40, 4), " +
                            "(5, 'BUS5', '105', 40, 5), " +
                            "(6, 'BUS6', '106', 40, 6)"
            );

            System.out.println("Database ready!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }
}
