package com.safetrack.main;

import com.safetrack.util.DatabaseConnection;
import com.safetrack.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Statement;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Start Login UI
            new LoginView().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // If using SQLite → OK
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT, role TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, busId INTEGER, seat INTEGER)");

            stmt.execute("INSERT OR IGNORE INTO users(id,email,password,role) VALUES(1,'admin@gmail.com','123','ADMIN')");
            stmt.execute("INSERT OR IGNORE INTO users(id,email,password,role) VALUES(2,'user@gmail.com','123','PASSENGER')");

            System.out.println("Database ready!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args); // VERY IMPORTANT
    }
}