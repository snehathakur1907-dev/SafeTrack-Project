package com.safetrack.view;

import com.safetrack.util.DatabaseConnection;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;

import java.sql.*;

/**
 * Main GUI class for the Admin Dashboard.
 * Handles the display and user interaction for managing Users, Buses, Routes, Bookings, and Emergency Contacts.
 * All database operations are strictly delegated to respective Data Access Objects (DAOs) 
 * to adhere closely to Object-Oriented Design principles and separation of concerns.
 */
public class AdminDashboardView extends Application {

    private BorderPane root;
    private VBox contentArea;
    private String activeMenu = "Dashboard";
    private Stage mainStage;
    private boolean isSidebarCollapsed = false;
    private int currentUserId = -1;

    public AdminDashboardView() {}

    public AdminDashboardView(int userId) {
        this.currentUserId = userId;
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        stage.setTitle("SafeTrack – Admin Dashboard");
        stage.setWidth(1100);
        stage.setHeight(680);
        stage.setResizable(true);

        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(buildMainArea());

        stage.setScene(new Scene(root));
        stage.show();
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────────
    /**
     * Constructs the collapsible navigation sidebar.
     * Includes navigation menu items for all main modules and a logout button.
     * @return VBox containing the configured sidebar.
     */
    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(isSidebarCollapsed ? 80 : 230);
        sidebar.setStyle("-fx-background-color: #1a1a2e;");

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");
        Label icon = new Label("\uD83D\uDE96");

        if (!isSidebarCollapsed) {
            logoBox.setAlignment(Pos.CENTER_LEFT);
            logoBox.setPadding(new Insets(28, 20, 24, 20));
            icon.setStyle("-fx-font-size: 28px;");
            Label appName = new Label("SafeTrack");
            appName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
            Label roleTag = new Label("ADMIN PANEL");
            roleTag.setStyle("-fx-font-size: 10px; -fx-text-fill: #e94560; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold;");
            logoBox.getChildren().addAll(icon, appName, roleTag);
        } else {
            logoBox.setAlignment(Pos.CENTER);
            logoBox.setPadding(new Insets(28, 0, 24, 0));
            icon.setStyle("-fx-font-size: 24px;");
            logoBox.getChildren().add(icon);
        }

        // Menu
        VBox menuBox = new VBox(3);
        menuBox.setPadding(new Insets(16, 10, 10, 10));
        menuBox.getChildren().addAll(
                menuItem("\uD83D\uDCCA", "Dashboard"),
                menuItem("\uD83D\uDC65", "Users"),
                menuItem("\uD83D\uDE8C", "Buses"),
                menuItem("\uD83C\uDFAB", "Bookings"),
                menuItem("\uD83D\uDDD3", "Routes"),
                menuItem("\uD83D\uDEA8", "Emergency")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        HBox logoutBtn = new HBox(10);
        logoutBtn.setCursor(javafx.scene.Cursor.HAND);
        logoutBtn.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;");
        Label logIc = new Label("\uD83D\uDEAA");
        logIc.setStyle("-fx-font-size: 15px;");

        if (!isSidebarCollapsed) {
            logoutBtn.setAlignment(Pos.CENTER_LEFT);
            logoutBtn.setPadding(new Insets(16, 18, 16, 18));
            Label logLb = new Label("Logout");
            logLb.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
            logoutBtn.getChildren().addAll(logIc, logLb);
        } else {
            logoutBtn.setAlignment(Pos.CENTER);
            logoutBtn.setPadding(new Insets(16, 0, 16, 0));
            logoutBtn.getChildren().add(logIc);
        }

        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: rgba(233,69,96,0.15); -fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;"));
        logoutBtn.setOnMouseClicked(e -> {
            try { new LoginView().start(mainStage); } catch (Exception ex) { ex.printStackTrace(); }
        });

        sidebar.getChildren().addAll(logoBox, menuBox, spacer, logoutBtn);
        return sidebar;
    }

    private HBox menuItem(String emoji, String label) {
        HBox item = new HBox(isSidebarCollapsed ? 0 : 12);
        item.setAlignment(isSidebarCollapsed ? Pos.CENTER : Pos.CENTER_LEFT);
        item.setPadding(isSidebarCollapsed ? new Insets(11, 0, 11, 0) : new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);

        boolean active = activeMenu.equals(label);
        item.setStyle("-fx-background-radius: 8;" + (active ? " -fx-background-color: #e94560;" : ""));

        Label ic = new Label(emoji);
        ic.setStyle("-fx-font-size: 15px;");

        if (!isSidebarCollapsed) {
            Label lb = new Label(label);
            lb.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI'; -fx-text-fill: " + (active ? "white" : "rgba(255,255,255,0.65)") + ";");
            item.getChildren().addAll(ic, lb);
        } else {
            item.getChildren().add(ic);
        }

        item.setOnMouseEntered(e -> {
            if (!activeMenu.equals(label)) item.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8;");
        });
        item.setOnMouseExited(e -> {
            if (!activeMenu.equals(label)) item.setStyle("-fx-background-radius: 8;");
        });
        item.setOnMouseClicked(e -> {
            activeMenu = label;
            root.setLeft(buildSidebar());
            loadPage(label);
        });

        return item;
    }

    // ── MAIN AREA ─────────────────────────────────────────────────────────────
    private VBox buildMainArea() {
        VBox main = new VBox(0);
        main.setStyle("-fx-background-color: #f0f2f5;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 28, 18, 28));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #eaeaea; -fx-border-width: 0 0 1 0;");

        Button menuBtn = new Button("\u2630"); // ☰ Hamburger Icon
        menuBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: #1a1a2e; -fx-padding: 0;");
        menuBtn.setOnAction(e -> {
            isSidebarCollapsed = !isSidebarCollapsed;
            root.setLeft(buildSidebar());
        });

        Label pageTitle = new Label("Dashboard Overview");
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminBadge = new Label("  \uD83D\uDC64  Admin  ");
        adminBadge.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 20; -fx-padding: 6 14; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;");
        adminBadge.setOnMouseClicked(e -> {
            activeMenu = "Profile";
            root.setLeft(buildSidebar());
            loadProfilePage();
        });

        topBar.getChildren().addAll(menuBtn, pageTitle, spacer, adminBadge);

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(28));
        contentArea.setStyle("-fx-font-smoothing-type: lcd;");

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f0f2f5; -fx-background-color: #f0f2f5;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        loadDashboardPage();

        main.getChildren().addAll(topBar, scroll);
        return main;
    }

    /**
     * Dynamically clears the current content area and loads the selected page view.
     * @param page The name of the module to load (e.g., "Dashboard", "Users", "Buses").
     */
    private void loadPage(String page) {
        contentArea.getChildren().clear();
        switch (page) {
            case "Dashboard" -> loadDashboardPage();
            case "Users"     -> loadUsersPage();
            case "Buses"     -> loadBusesPage();
            case "Bookings"  -> loadBookingsPage();
            case "Routes"    -> loadRoutesPage();
            case "Emergency" -> loadEmergencyPage();
            case "Profile"   -> loadProfilePage();
        }
    }

    // ── PROFILE PAGE ──────────────────────────────────────────────────────────
    private void loadProfilePage() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(sectionLabel("My Profile"));

        VBox form = new VBox(15);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        form.setEffect(new DropShadow(10, 0, 4, Color.gray(0, 0.05)));

        TextField nameFd = formField("Name", 300);
        TextField userFd = formField("Username", 300);
        TextField emailFd = formField("Email", 300);
        PasswordField passFd = new PasswordField();
        passFd.setPromptText("New Password (leave blank to keep current)");
        passFd.setPrefWidth(300);
        passFd.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 6; -fx-border-color: #e2e2e2; -fx-border-radius: 6; -fx-padding: 8; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        VBox passBox = new VBox(5, new Label("Password (Optional)"), passFd);
        Label successMsg = new Label("");
        successMsg.setStyle("-fx-text-fill: #27ae60; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        Button saveBtn = actionButton("Save Changes", "#e94560");

        com.safetrack.dao.UserDAO userDAO = new com.safetrack.dao.UserDAO();
        String[] details = userDAO.getUserProfileDetails(currentUserId);
        if (details != null) {
            nameFd.setText(details[0]);
            userFd.setText(details[1]);
            emailFd.setText(details[2]);
        }

        saveBtn.setOnAction(e -> {
            String name = nameFd.getText().trim();
            String user = userFd.getText().trim();
            String email = emailFd.getText().trim();
            String pass = passFd.getText().trim();

            if (name.isEmpty() || user.isEmpty() || email.isEmpty()) {
                successMsg.setText("\u2718 Error: All fields except password are required!");
                successMsg.setStyle("-fx-text-fill: #c0392b; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                successMsg.setText("\u2718 Error: Invalid email format!");
                successMsg.setStyle("-fx-text-fill: #c0392b; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                return;
            }

            try {
                boolean success = userDAO.updateProfile(
                    currentUserId, 
                    name, 
                    user, 
                    email, 
                    pass
                );
                if (success) {
                    successMsg.setText("\u2714 Profile updated successfully!");
                    // Keep the password field hidden again
                    passFd.clear();
                } else {
                    successMsg.setText("\u2718 Error: Could not update profile.");
                    successMsg.setStyle("-fx-text-fill: #c0392b; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                }
            } catch(Exception ex) {
                successMsg.setText("\u2718 Error: " + ex.getMessage());
                successMsg.setStyle("-fx-text-fill: #c0392b; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
            }
        });

        form.getChildren().addAll(
                new VBox(5, new Label("Name"), nameFd),
                new VBox(5, new Label("Username"), userFd),
                new VBox(5, new Label("Email"), emailFd),
                passBox, saveBtn, successMsg
        );
        contentArea.getChildren().add(form);
    }

    // ── DASHBOARD PAGE ────────────────────────────────────────────────────────
    private void loadDashboardPage() {
        int[] stats = new com.safetrack.dao.DashboardDAO().getDashboardStats();
        int userCount = stats[0], busCount = stats[1], bookingCount = stats[2], routeCount = stats[3];

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
                statCard("Total Users",    String.valueOf(userCount),    "#e94560", "\uD83D\uDC65"),
                statCard("Total Buses",    String.valueOf(busCount),     "#0f3460", "\uD83D\uDE8C"),
                statCard("Total Bookings", String.valueOf(bookingCount), "#27ae60", "\uD83C\uDFAB"),
                statCard("Active Routes",  String.valueOf(routeCount),   "#e67e22", "\uD83D\uDDD3")
        );

        Label recentLbl = sectionLabel("Recent Users");
        VBox tableCard = wrapCard(buildUsersTable(5));
        contentArea.getChildren().addAll(cards, recentLbl, tableCard);
    }

    // private int getCount removed as it's now in DashboardDAO

    private VBox statCard(String label, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        card.setEffect(new DropShadow(12, 0, 3, Color.gray(0, 0.08)));
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 22px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        top.getChildren().addAll(ic, sp);

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + color + "; -fx-font-family: 'Segoe UI';");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #999; -fx-font-family: 'Segoe UI';");

        card.getChildren().addAll(top, val, lbl);
        return card;
    }

    // ── USERS PAGE ────────────────────────────────────────────────────────────
    private void loadUsersPage() {
        contentArea.getChildren().addAll(sectionLabel("Manage Users"), wrapCard(buildUsersTable(-1)));
    }

    private VBox buildUsersTable(int limit) {
        VBox box = new VBox(0);
        box.getChildren().add(tableRow(true, "ID", "Name", "Username", "Email", "Role", "Actions"));
        com.safetrack.dao.UserDAO userDAO = new com.safetrack.dao.UserDAO();
        try {
            java.util.List<String[]> users = userDAO.getAllUsers(limit);
            boolean alt = false;
            for (String[] u : users) {
                HBox row = tableRow(false,
                        u[0],
                        nvl(u[1]),
                        nvl(u[2]),
                        nvl(u[3]),
                        nvl(u[4]),
                        actionButtons("users", u[0]));
                if (alt) row.setStyle(row.getStyle() + "-fx-background-color: #fafafa;");
                box.getChildren().add(row);
                alt = !alt;
            }
        } catch (Exception ex) {
            box.getChildren().add(errorLabel(ex.getMessage()));
        }
        return box;
    }

    // ── BUSES PAGE ────────────────────────────────────────────────────────────
    private void loadBusesPage() {
        ensureTable("buses",
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, name TEXT, number TEXT, capacity INTEGER");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        form.setEffect(new DropShadow(12, 0, 3, Color.gray(0, 0.07)));

        form.getChildren().add(sectionLabel("Add New Bus"));
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        TextField busName = formField("Bus Name", 150);
        TextField busNum  = formField("Bus Number", 150);
        TextField cap     = formField("Capacity", 80);
        TextField timeFd = formField("Time (10:00 AM)", 110);
        ComboBox<String> routeBox = new ComboBox<>();
        routeBox.setPromptText("Select Route");
        routeBox.setPrefWidth(180);
        routeBox.setPrefHeight(40);
        routeBox.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 8; " +
                "-fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-border-width: 1.5; " +
                "-fx-font-size: 13px; -fx-font-family: 'Segoe UI';");

        com.safetrack.dao.RouteDAO routeDAO = new com.safetrack.dao.RouteDAO();
        for (String[] r : routeDAO.getAllRoutes()) {
            routeBox.getItems().add(r[0] + " - " + r[1] + " to " + r[2]);
        }

        com.safetrack.dao.BusDAO busDAO = new com.safetrack.dao.BusDAO();
        Button addBtn = actionButton("+ Add Bus", "#e94560");
        addBtn.setOnAction(e -> {
            // Reset error styles
            busName.setStyle(busName.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            routeBox.setStyle(routeBox.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            cap.setStyle(cap.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            timeFd.setStyle(timeFd.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));

            String nameTxt = busName.getText().trim();
            String timeTxt = timeFd.getText().trim();
            if(timeTxt.isEmpty()) timeTxt = "10:00 AM";

            if (nameTxt.isEmpty() || routeBox.getValue() == null) {
                if (nameTxt.isEmpty()) busName.setStyle(busName.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                if (routeBox.getValue() == null) routeBox.setStyle(routeBox.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                return;
            }

            try {
                int rId = Integer.parseInt(routeBox.getValue().split(" - ")[0]);
                int parsedCap = 0;
                String capTxt = cap.getText().trim();
                if (!capTxt.isEmpty()) {
                    parsedCap = Integer.parseInt(capTxt);
                    if (parsedCap < 1) throw new NumberFormatException(); // Bus capacity must be positive
                }
                
                busDAO.addBus(nameTxt, busNum.getText().trim(), parsedCap, timeTxt, rId);
                busName.clear(); busNum.clear(); cap.clear(); timeFd.clear(); routeBox.setValue(null);
                loadPage("Buses");
            } catch (NumberFormatException ex) {
                cap.setStyle(cap.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        row.getChildren().addAll(busName, busNum, cap, timeFd, routeBox, addBtn);
        form.getChildren().add(row);

        VBox table = new VBox(0);
        table.getChildren().add(tableRow(true, "ID", "Bus Name", "Number", "Capacity", "Route", "Actions"));
        try {
            java.util.List<String[]> buses = busDAO.getAllBusesWithRoutes();
            boolean alt = false;
            for (String[] bus : buses) {
                HBox r = tableRow(false, bus[0], bus[1],
                        nvl(bus[2]), nvl(bus[3]), bus[4], actionButtons("buses", bus[0]));
                if (alt) r.setStyle(r.getStyle() + "-fx-background-color: #fafafa;");
                table.getChildren().add(r);
                alt = !alt;
            }
            if (table.getChildren().size() == 1) table.getChildren().add(emptyLabel("No buses added yet."));
        } catch (Exception ex) { table.getChildren().add(errorLabel(ex.getMessage())); }

        contentArea.getChildren().addAll(sectionLabel("Manage Buses"), form, wrapCard(table));
    }



    // ── BOOKINGS PAGE ─────────────────────────────────────────────────────────
    private void loadBookingsPage() {
        VBox box = new VBox(0);
        box.getChildren().add(tableRow(true, "ID", "User", "Bus", "Route", "Date", "Time", "Seat", "Pay", "Pay Status", "Ride Status", "Actions"));
        com.safetrack.dao.TicketDAO ticketDAO = new com.safetrack.dao.TicketDAO();
        try {
            java.util.List<String[]> bookings = ticketDAO.getAllBookings();
            boolean alt = false;
            for (String[] bk : bookings) {
                // Formatted bk: [id, user_id, bus_id, bus_name, routeStr, journey_date, journey_time, seat, pay_method, pay_status, ride_status]
                HBox row = tableRow(false, bk[0], bk[1], bk[3], bk[4], bk[5], bk[6], bk[7], bk[8], bk[9], bk[10], actionButtons("bookings", bk[0], bk[10]));
                if (alt) row.setStyle(row.getStyle() + "-fx-background-color: #fafafa;");
                box.getChildren().add(row);
                alt = !alt;
            }
            if (box.getChildren().size() == 1) box.getChildren().add(emptyLabel("No bookings found."));
        } catch (Exception ex) { box.getChildren().add(errorLabel(ex.getMessage())); }

        contentArea.getChildren().addAll(sectionLabel("All Bookings"), wrapCard(box));
    }

    // ── ROUTES PAGE ───────────────────────────────────────────────────────────
    private void loadRoutesPage() {
        ensureTable("routes",
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, origin TEXT, destination TEXT, fare REAL");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        form.setEffect(new DropShadow(12, 0, 3, Color.gray(0, 0.07)));
        form.getChildren().add(sectionLabel("Add New Route"));

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        TextField from  = formField("From (Origin)", 180);
        TextField to    = formField("To (Destination)", 180);
        TextField fare  = formField("Fare (Rs.)", 100);
        com.safetrack.dao.RouteDAO routeDAO = new com.safetrack.dao.RouteDAO();
        Button addBtn   = actionButton("+ Add Route", "#0f3460");
        addBtn.setOnAction(e -> {
            // Reset styles
            from.setStyle(from.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            to.setStyle(to.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            fare.setStyle(fare.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));

            String fromTxt = from.getText().trim();
            String toTxt = to.getText().trim();
            
            if (fromTxt.isEmpty() || toTxt.isEmpty()) {
                if (fromTxt.isEmpty()) from.setStyle(from.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                if (toTxt.isEmpty()) to.setStyle(to.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                return;
            }

            try {
                String fareTxt = fare.getText().trim();
                double parsedFare = 0;
                if (!fareTxt.isEmpty()) {
                    parsedFare = Double.parseDouble(fareTxt);
                    if (parsedFare < 0) throw new NumberFormatException();
                }
                routeDAO.addRoute(fromTxt, toTxt, parsedFare);
                from.clear(); to.clear(); fare.clear();
                loadPage("Routes");
            } catch (NumberFormatException ex) {
                fare.setStyle(fare.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        row.getChildren().addAll(from, to, fare, addBtn);
        form.getChildren().add(row);

        VBox table = new VBox(0);
        table.getChildren().add(tableRow(true, "ID", "From", "To", "Fare (Rs.)", "Actions"));
        try {
            java.util.List<String[]> routes = routeDAO.getAllRoutes();
            boolean alt = false;
            for (String[] rt : routes) {
                HBox r = tableRow(false, rt[0], rt[1],
                        nvl(rt[2]), "Rs. " + rt[3], actionButtons("routes", rt[0]));
                if (alt) r.setStyle(r.getStyle() + "-fx-background-color: #fafafa;");
                table.getChildren().add(r);
                alt = !alt;
            }
            if (table.getChildren().size() == 1) table.getChildren().add(emptyLabel("No routes added yet."));
        } catch (Exception ex) { table.getChildren().add(errorLabel(ex.getMessage())); }

        contentArea.getChildren().addAll(sectionLabel("Manage Routes"), form, wrapCard(table));
    }

    // ── EMERGENCY PAGE ────────────────────────────────────────────────────────
    private void loadEmergencyPage() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        form.setEffect(new DropShadow(12, 0, 3, Color.gray(0, 0.07)));

        form.getChildren().add(sectionLabel("Add New Contact"));
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        TextField cName = formField("Name / Dept", 150);
        TextField cPhone = formField("Phone Number", 150);
        TextField cRel = formField("Emoji (e.g. 🚒)", 100);
        com.safetrack.dao.EmergencyDAO emergencyDAO = new com.safetrack.dao.EmergencyDAO();
        Button addBtn = actionButton("+ Add", "#e94560");
        addBtn.setOnAction(e -> {
            cName.setStyle(cName.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            cPhone.setStyle(cPhone.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #e2e2e2;"));
            
            String nameTxt = cName.getText().trim();
            String phoneTxt = cPhone.getText().trim();
            
            if (nameTxt.isEmpty() || phoneTxt.isEmpty()) {
                if (nameTxt.isEmpty()) cName.setStyle(cName.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                if (phoneTxt.isEmpty()) cPhone.setStyle(cPhone.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                return;
            }

            if (!phoneTxt.matches("^[0-9+\\-() ]+$")) {
                cPhone.setStyle(cPhone.getStyle().replace("-fx-border-color: #e2e2e2;", "-fx-border-color: #e74c3c;"));
                return;
            }

            try {
                String relation = cRel.getText().trim().isEmpty() ? "📞" : cRel.getText().trim();
                emergencyDAO.addEmergencyContact(nameTxt, phoneTxt, relation);
                cName.clear(); cPhone.clear(); cRel.clear();
                loadPage("Emergency");
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        row.getChildren().addAll(cName, cPhone, cRel, addBtn);
        form.getChildren().add(row);

        VBox rowList = new VBox(12);
        try {
            java.util.List<String[]> contacts = emergencyDAO.getAllEmergencyContacts();
            for (String[] contact : contacts) {
                String idStr = contact[0];
                String nameStr = contact[1];
                String phoneStr = contact[2];
                String iconStr = contact[3] != null && !contact[3].isEmpty() ? contact[3] : "📞";

                HBox card = new HBox(16);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(16, 20, 16, 20));
                card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #f0f0f0; -fx-border-radius: 10; -fx-border-width: 1;");

                Label ic = new Label(iconStr);
                ic.setStyle("-fx-font-size: 26px;");

                VBox info = new VBox(3);
                Label nameLbl = new Label(nameStr);
                nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");
                Label numLbl = new Label(phoneStr);
                numLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
                info.getChildren().addAll(nameLbl, numLbl);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                card.getChildren().addAll(ic, info, spacer, actionButtons("emergency_contacts", idStr));
                rowList.getChildren().add(card);
            }
            if (rowList.getChildren().isEmpty()) rowList.getChildren().add(emptyLabel("No emergency contacts found."));
        } catch (Exception ex) { rowList.getChildren().add(errorLabel(ex.getMessage())); }

        contentArea.getChildren().addAll(form, rowList);
    }

    // ── DYNAMIC CRUD HELPERS ──────────────────────────────────────────────────
    private HBox actionButtons(String table, String id, String... extra) {
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);

        // For bookings, show a "Mark Completed" button if ride is UPCOMING
        if (table.equals("bookings")) {
            if (extra.length > 0 && "UPCOMING".equals(extra[0])) {
                Button completeBtn = new Button("✔ Finish");
                completeBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 4 8;");
                completeBtn.setOnAction(e -> {
                    new com.safetrack.dao.TicketDAO().updateRideStatus(Integer.parseInt(id), "COMPLETED");
                    loadPage("Bookings");
                });
                actions.getChildren().add(completeBtn);
            }
            return actions;
        }

        Button editBtn = new Button("✎ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 4 8;");
        editBtn.setOnAction(e -> showEditDialog(table, id));

        Button delBtn = new Button("✖");
        delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 4 8;");
        delBtn.setOnAction(e -> deleteRecord(table, id));

        actions.getChildren().addAll(editBtn, delBtn);
        return actions;
    }

    /**
     * Deletes a record by dispatching to the correct typed DAO method.
     * BEFORE: used raw SQL "DELETE FROM "+table+" WHERE id="+id (bypassed DAO layer, injection risk).
     * AFTER: every table routes through its dedicated DAO — no raw SQL in this view.
     *
     * @param table the logical name used to identify which DAO to use
     * @param id    string primary key of the record to delete
     */
    private void deleteRecord(String table, String id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete record #" + id + " from " + table + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() != ButtonType.YES) return;

        int recordId = Integer.parseInt(id);
        try {
            switch (table) {
                case "users":
                    new com.safetrack.dao.UserDAO().deleteUser(recordId);
                    break;
                case "buses":
                    new com.safetrack.dao.BusDAO().deleteBus(recordId);
                    break;
                case "routes":
                    new com.safetrack.dao.RouteDAO().deleteRoute(recordId);
                    break;
                case "emergency_contacts":
                    new com.safetrack.dao.EmergencyDAO().deleteContact(recordId);
                    break;
                default:
                    System.err.println("Unknown table for deletion: " + table);
                    return;
            }
            loadPage(activeMenu);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void showEditDialog(String table, String id) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Record");
        dialog.setHeaderText("Editing " + table + " (ID: " + id + ")");
        ButtonType saveBtnType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        java.util.List<TextField> fields = new java.util.ArrayList<>();
        java.util.List<String> cols = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + table + " WHERE id = " + id);
            if (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                int rowIdx = 0;
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String col = meta.getColumnName(i);
                    if (col.equalsIgnoreCase("id")) continue; // Immutable
                    cols.add(col);
                    grid.add(new Label(col.substring(0, 1).toUpperCase() + col.substring(1) + ":"), 0, rowIdx);
                    TextField field = new TextField(rs.getString(i));
                    field.setPrefWidth(250);
                    fields.add(field);
                    grid.add(field, 1, rowIdx);
                    rowIdx++;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
                    for (int i = 0; i < cols.size(); i++) {
                        sql.append(cols.get(i)).append(" = ?");
                        if (i < cols.size() - 1) sql.append(", ");
                    }
                    sql.append(" WHERE id = ").append(id);
                    PreparedStatement ps = conn.prepareStatement(sql.toString());
                    for (int i = 0; i < fields.size(); i++) ps.setString(i + 1, fields.get(i).getText());
                    ps.executeUpdate();
                    loadPage(activeMenu);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ── SHARED HELPERS ────────────────────────────────────────────────────────
    private HBox tableRow(boolean header, Object... cols) {
        HBox row = new HBox(0);
        row.setStyle(header
                ? "-fx-background-color: #0f3460; -fx-padding: 12 16;"
                : "-fx-padding: 11 16; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
        for (Object col : cols) {
            if (col instanceof javafx.scene.Node) {
                HBox cell = new HBox((javafx.scene.Node) col);
                cell.setAlignment(Pos.CENTER_LEFT);
                cell.setPrefWidth(200);
                cell.setMinWidth(50);
                cell.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(cell, Priority.ALWAYS);
                row.getChildren().add(cell);
            } else {
                String text = col != null ? col.toString() : "-";
                Label lbl = new Label(text);
                lbl.setStyle(header
                        ? "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';"
                        : "-fx-text-fill: #444; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
                lbl.setPrefWidth(200);
                lbl.setMinWidth(50);
                lbl.setMaxWidth(Double.MAX_VALUE);
                lbl.setWrapText(true);
                HBox.setHgrow(lbl, Priority.ALWAYS);
                row.getChildren().add(lbl);
            }
        }
        return row;
    }

    private VBox wrapCard(VBox content) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        card.setEffect(new DropShadow(12, 0, 3, Color.gray(0, 0.07)));
        card.getChildren().add(content);
        return card;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; " +
                "-fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");
        return lbl;
    }

    private TextField formField(String prompt, double width) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(width);
        tf.setPrefHeight(40);
        tf.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 8; " +
                "-fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-border-width: 1.5; " +
                "-fx-padding: 8 12; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
        return tf;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                "-fx-cursor: hand; -fx-font-family: 'Segoe UI'; -fx-padding: 0 20;");
        return btn;
    }

    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px; " +
                "-fx-padding: 16; -fx-font-family: 'Segoe UI';");
        return lbl;
    }

    private Label errorLabel(String text) {
        Label lbl = new Label("Error: " + text);
        lbl.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px; -fx-padding: 10;");
        return lbl;
    }

    private String nvl(String s) { return s != null ? s : "-"; }

    private void ensureTable(String name, String cols) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + name + " (" + cols + ")");
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) { launch(args); }
}

