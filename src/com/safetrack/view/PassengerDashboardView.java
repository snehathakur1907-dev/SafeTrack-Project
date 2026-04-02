package com.safetrack.view;

import com.safetrack.util.DatabaseConnection;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.*;
import javafx.stage.Stage;

import java.sql.*;

public class PassengerDashboardView extends Application {

    private BorderPane root;
    private VBox contentArea;
    private String activeMenu = "Home";
    private Stage mainStage;

    private boolean isSidebarCollapsed = false;

    // Will be set after login — for now use a default
    private int loggedInUserId = 2;
    private String loggedInName = "Passenger";

    public PassengerDashboardView() {}

    public PassengerDashboardView(int userId) {
        this.loggedInUserId = userId;
    }

    public void setUser(int userId, String name) {
        this.loggedInUserId = userId;
        this.loggedInName   = name;
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE id = ?");
            ps.setInt(1, loggedInUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loggedInName = rs.getString("name");
            }
        } catch (Exception e) { e.printStackTrace(); }

        stage.setTitle("SafeTrack – Passenger Dashboard");
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
    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(isSidebarCollapsed ? 80 : 230);
        sidebar.setStyle("-fx-background-color: #0f3460;");

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
            Label roleTag = new Label("PASSENGER");
            roleTag.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ecca3; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold;");
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
                menuItem("\uD83C\uDFE0", "Home"),
                menuItem("\uD83D\uDD0D", "Browse Routes"),
                menuItem("\uD83C\uDFAB", "Book a Ride"),
                menuItem("\uD83D\uDCCB", "My Bookings"),
                menuItem("\uD83D\uDEA8", "Emergency")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User info + logout
        VBox bottomBox = new VBox(0);
        bottomBox.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;");

        HBox userInfo = new HBox(10);
        userInfo.setAlignment(isSidebarCollapsed ? Pos.CENTER : Pos.CENTER_LEFT);
        userInfo.setPadding(isSidebarCollapsed ? new Insets(14, 0, 8, 0) : new Insets(14, 18, 8, 18));
        userInfo.setCursor(javafx.scene.Cursor.HAND);
        Label avatar = new Label("\uD83D\uDC64");
        avatar.setStyle("-fx-font-size: 18px;");

        if (!isSidebarCollapsed) {
            VBox nameBox = new VBox(2);
            Label nameLabel = new Label(loggedInName);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
            Label roleLabel = new Label("Passenger");
            roleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 10px; -fx-font-family: 'Segoe UI';");
            nameBox.getChildren().addAll(nameLabel, roleLabel);
            userInfo.getChildren().addAll(avatar, nameBox);
        } else {
            userInfo.getChildren().add(avatar);
        }

        userInfo.setOnMouseClicked(e -> {
            activeMenu = "Profile";
            root.setLeft(buildSidebar());
            loadPage("Profile"); // Wait, loadPage accepts Profile because we will add it shortly
        });

        HBox logoutBtn = new HBox(10);
        logoutBtn.setAlignment(isSidebarCollapsed ? Pos.CENTER : Pos.CENTER_LEFT);
        logoutBtn.setPadding(isSidebarCollapsed ? new Insets(8, 0, 14, 0) : new Insets(8, 18, 14, 18));
        logoutBtn.setCursor(javafx.scene.Cursor.HAND);
        Label logIc = new Label("\uD83D\uDEAA");
        logIc.setStyle("-fx-font-size: 14px;");
        Label logLb = new Label("Logout");
        logLb.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        if (!isSidebarCollapsed) {
            logoutBtn.getChildren().addAll(logIc, logLb);
        } else {
            logoutBtn.getChildren().add(logIc);
        }

        logoutBtn.setOnMouseEntered(e -> logLb.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';"));
        logoutBtn.setOnMouseExited(e -> logLb.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 12px; -fx-font-family: 'Segoe UI';"));
        logoutBtn.setOnMouseClicked(e -> {
            try { new LoginView().start(mainStage); } catch (Exception ex) { ex.printStackTrace(); }
        });

        bottomBox.getChildren().addAll(userInfo, logoutBtn);
        sidebar.getChildren().addAll(logoBox, menuBox, spacer, bottomBox);
        return sidebar;
    }

    private HBox menuItem(String emoji, String label) {
        HBox item = new HBox(isSidebarCollapsed ? 0 : 12);
        item.setAlignment(isSidebarCollapsed ? Pos.CENTER : Pos.CENTER_LEFT);
        item.setPadding(isSidebarCollapsed ? new Insets(11, 0, 11, 0) : new Insets(11, 16, 11, 16));
        item.setCursor(javafx.scene.Cursor.HAND);

        boolean active = activeMenu.equals(label);
        item.setStyle(active ? "-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 8;" : "-fx-background-radius: 8;");

        Label ic = new Label(emoji);
        ic.setStyle("-fx-font-size: 15px;");

        if (!isSidebarCollapsed) {
            Label lb = new Label(label);
            lb.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI'; -fx-text-fill: " + (active ? "white" : "rgba(255,255,255,0.65)") + ";");

            if (active) {
                Rectangle accent = new Rectangle(3, 20);
                accent.setFill(Color.web("#4ecca3"));
                accent.setArcWidth(3); accent.setArcHeight(3);
                item.getChildren().addAll(accent, ic, lb);
            } else {
                item.getChildren().addAll(ic, lb);
            }
        } else {
            item.getChildren().add(ic);
        }

        item.setOnMouseEntered(e -> {
            if (!activeMenu.equals(label))
                item.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8;");
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

        Label pageTitle = new Label("Welcome, " + loggedInName + "!");
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label currentBadge = new Label("  \uD83D\uDC64  Profile  ");
        currentBadge.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 20; -fx-padding: 6 14; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;");
        currentBadge.setOnMouseClicked(e -> {
            activeMenu = "Profile";
            root.setLeft(buildSidebar());
            loadPage("Profile");
        });

        topBar.getChildren().addAll(menuBtn, pageTitle, spacer, currentBadge);

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(28));
        contentArea.setStyle("-fx-font-smoothing-type: lcd;");

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f0f2f5; -fx-background-color: #f0f2f5;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        loadHomePage();

        main.getChildren().addAll(topBar, scroll);
        return main;
    }

    private void loadPage(String page) {
        contentArea.getChildren().clear();
        switch (page) {
            case "Home"          -> loadHomePage();
            case "Browse Routes" -> loadRoutesPage();
            case "Book a Ride"   -> loadBookPage();
            case "My Bookings"   -> loadMyBookingsPage();
            case "Emergency"     -> loadEmergencyPage();
            case "Profile"       -> loadProfilePage();
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

        Button saveBtn = quickBtn("Save Changes", "#e94560");

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name, username, email FROM users WHERE id = ?");
            ps.setInt(1, loggedInUserId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                nameFd.setText(rs.getString("name"));
                userFd.setText(rs.getString("username"));
                emailFd.setText(rs.getString("email"));
            }
        } catch(Exception e) { e.printStackTrace(); }

        saveBtn.setOnAction(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String q = passFd.getText().isEmpty() ? "UPDATE users SET name=?, username=?, email=? WHERE id=?"
                        : "UPDATE users SET name=?, username=?, email=?, password=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(q);
                ps.setString(1, nameFd.getText().trim());
                ps.setString(2, userFd.getText().trim());
                ps.setString(3, emailFd.getText().trim());
                if (passFd.getText().isEmpty()) {
                    ps.setInt(4, loggedInUserId);
                } else {
                    ps.setString(4, passFd.getText().trim());
                    ps.setInt(5, loggedInUserId);
                }
                ps.executeUpdate();
                successMsg.setText("\u2714 Profile updated successfully!");
                loggedInName = nameFd.getText().trim();

                // Keep the password field hidden again
                passFd.clear();
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

    // ── HOME PAGE ─────────────────────────────────────────────────────────────
    private void loadHomePage() {
        // Welcome banner
        VBox banner = new VBox(8);
        banner.setPadding(new Insets(28, 32, 28, 32));
        banner.setStyle("-fx-background-color: linear-gradient(to right, #0f3460, #16213e); " +
                "-fx-background-radius: 14;");
        Label hi = new Label("Hello, " + loggedInName + "! \uD83D\uDC4B");
        hi.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-font-family: 'Segoe UI';");
        Label sub = new Label("Where would you like to go today?");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.65); " +
                "-fx-font-family: 'Segoe UI';");

        HBox quickBtns = new HBox(12);
        quickBtns.setPadding(new Insets(12, 0, 0, 0));
        Button bookBtn   = quickBtn("Book a Ride", "#e94560");
        Button routeBtn  = quickBtn("Browse Routes", "rgba(255,255,255,0.15)");
        bookBtn.setOnAction(e -> { activeMenu = "Book a Ride"; root.setLeft(buildSidebar()); loadPage("Book a Ride"); });
        routeBtn.setOnAction(e -> { activeMenu = "Browse Routes"; root.setLeft(buildSidebar()); loadPage("Browse Routes"); });
        quickBtns.getChildren().addAll(bookBtn, routeBtn);
        banner.getChildren().addAll(hi, sub, quickBtns);

        // Quick stats
        int myBookings = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM bookings WHERE user_id = " + loggedInUserId);
            if (rs.next()) myBookings = rs.getInt(1);
        } catch (Exception ignored) {}

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                miniCard("My Bookings",  String.valueOf(myBookings), "#e94560", "\uD83C\uDFAB"),
                miniCard("Available Buses", "Active",   "#27ae60",  "\uD83D\uDE8C"),
                miniCard("Support",       "24/7",       "#0f3460",  "\uD83D\uDCDE")
        );

        // Recent bookings
        Label recentLbl = sectionLabel("My Recent Bookings");
        VBox recentTable = buildMyBookingsTable(3);

        contentArea.getChildren().addAll(banner, statsRow, recentLbl, wrapCard(recentTable));
    }

    private Button quickBtn(String text, String bg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                "-fx-cursor: hand; -fx-font-family: 'Segoe UI'; -fx-padding: 10 22;");
        return btn;
    }

    private VBox miniCard(String label, String value, String color, String icon) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        card.setEffect(new DropShadow(10, 0, 3, Color.gray(0, 0.07)));
        HBox.setHgrow(card, Priority.ALWAYS);

        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 22px;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + color + "; -fx-font-family: 'Segoe UI';");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #999; -fx-font-family: 'Segoe UI';");
        card.getChildren().addAll(ic, val, lbl);
        return card;
    }

    // ── BROWSE ROUTES ─────────────────────────────────────────────────────────
    private void loadRoutesPage() {
        contentArea.getChildren().add(sectionLabel("Available Routes"));

        VBox box = new VBox(0);
        box.getChildren().add(tableRow(true, "ID", "From", "To", "Fare (Rs.)"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS routes " +
                            "(id INTEGER PRIMARY KEY AUTO_INCREMENT, source TEXT, destination TEXT, fare REAL)");
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM routes ORDER BY id");
            boolean alt = false;
            while (rs.next()) {
                HBox row = tableRow(false, rs.getString("id"),
                        rs.getString("source"), rs.getString("destination"),
                        "Rs. " + rs.getString("fare"));
                if (alt) row.setStyle(row.getStyle() + "-fx-background-color: #fafafa;");
                box.getChildren().add(row);
                alt = !alt;
            }
            if (box.getChildren().size() == 1)
                box.getChildren().add(emptyLabel("No routes available yet."));
        } catch (Exception ex) {
            box.getChildren().add(errorLabel(ex.getMessage()));
        }

        contentArea.getChildren().add(wrapCard(box));
    }

    // ── BOOK A RIDE ───────────────────────────────────────────────────────────
    private void loadBookPage() {
        contentArea.getChildren().add(sectionLabel("Book a Ride"));

        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(24));
        formCard.setMaxWidth(600);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        formCard.setEffect(new DropShadow(10, 0, 3, Color.gray(0, 0.08)));

        Label instruc = new Label("Select your journey details and pick an available seat.");
        instruc.setStyle("-fx-font-size: 13px; -fx-text-fill: #777; -fx-font-family: 'Segoe UI';");

        com.safetrack.controller.BookingController controller = new com.safetrack.controller.BookingController();

        // ── Selection Fields ──
        // Row 1: Route selector
        VBox routeBox = new VBox(6);
        Label routeLbl = new Label("Select Destination");
        routeLbl.setStyle(fieldLabelStyle());
        ComboBox<String> routeSelect = new ComboBox<>();
        routeSelect.setPromptText("Choose your destination");
        routeSelect.setPrefWidth(500);
        routeSelect.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 6; -fx-border-color: #e2e2e2; -fx-border-radius: 6; -fx-border-width: 1.5; -fx-font-size: 13px;");
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT id, source, destination FROM routes");
            while(rs.next()) {
                routeSelect.getItems().add(rs.getInt("id") + " - " + rs.getString("source") + " \u2192 " + rs.getString("destination"));
            }
        } catch (Exception ignored) {}
        routeBox.getChildren().addAll(routeLbl, routeSelect);

        // Row 2: Bus, Date, Time
        HBox topRow = new HBox(15);
        
        VBox busBox = new VBox(6);
        Label busLbl = new Label("Select Bus");
        busLbl.setStyle(fieldLabelStyle());
        ComboBox<String> busSelect = new ComboBox<>();
        busSelect.setPromptText("Select a bus");
        busSelect.setPrefWidth(300);
        busSelect.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 6; -fx-border-color: #e2e2e2; -fx-border-radius: 6; -fx-font-size: 13px;");
        busBox.getChildren().addAll(busLbl, busSelect);

        // Populate buses when a route is selected
        routeSelect.setOnAction(ev -> {
            busSelect.getItems().clear();
            if(routeSelect.getValue() != null) {
                int rId = Integer.parseInt(routeSelect.getValue().split(" - ")[0]);
                try (Connection c = DatabaseConnection.getConnection()) {
                    ResultSet rs = c.createStatement().executeQuery("SELECT id, name, capacity FROM buses WHERE route_id = " + rId);
                    while(rs.next()) {
                        busSelect.getItems().add(rs.getInt("id") + " - " + rs.getString("name") + " (" + rs.getInt("capacity") + " seats)");
                    }
                } catch(Exception ignored) {}
            }
        });

        VBox dateBox = new VBox(6);
        Label dateLbl = new Label("Journey Date");
        dateLbl.setStyle(fieldLabelStyle());
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(140);
        datePicker.setStyle("-fx-background-color: #f4f5f7; -fx-font-size: 13px;");
        dateBox.getChildren().addAll(dateLbl, datePicker);

        VBox timeBox = new VBox(6);
        Label timeLbl = new Label("Departure Time");
        timeLbl.setStyle(fieldLabelStyle());
        ComboBox<String> timeSelect = new ComboBox<>();
        timeSelect.getItems().addAll("08:00 AM", "10:00 AM", "01:00 PM", "04:00 PM", "08:00 PM", "10:00 PM");
        timeSelect.setPromptText("Time");
        timeSelect.setPrefWidth(110);
        timeSelect.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 6; -fx-border-color: #e2e2e2; -fx-border-radius: 6; -fx-font-size: 13px;");
        timeBox.getChildren().addAll(timeLbl, timeSelect);

        topRow.getChildren().addAll(busBox, dateBox, timeBox);

        // ── Seat Grid ──
        VBox seatBox = new VBox(6);
        Label seatLbl = new Label("Select a Seat");
        seatLbl.setStyle(fieldLabelStyle());

        Label remainingLbl = new Label("Remaining Seats: -");
        remainingLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555; -fx-font-weight: bold;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(8); seatGrid.setVgap(8);
        seatBox.getChildren().addAll(seatLbl, remainingLbl, seatGrid);

        Label msgLabel = new Label("");
        msgLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        final int[] selectedSeatNum = {-1};
        final Button[] lastSelectedBtn = {null};

        javafx.event.EventHandler<javafx.event.ActionEvent> reloadAction = ev -> {
            seatGrid.getChildren().clear();
            selectedSeatNum[0] = -1;
            lastSelectedBtn[0] = null;
            msgLabel.setText("");

            if(busSelect.getValue() == null || datePicker.getValue() == null || timeSelect.getValue() == null) {
                remainingLbl.setText("Please select Bus, Date, and Time.");
                return;
            }
            
            int busId = Integer.parseInt(busSelect.getValue().split(" - ")[0]);
            String capStr = busSelect.getValue();
            int capacity = Integer.parseInt(capStr.substring(capStr.indexOf("(") + 1, capStr.indexOf(" seats)")));

            java.util.Set<Integer> bookedSeats = controller.getBookedSeats(busId, datePicker.getValue().toString(), timeSelect.getValue());

            int row = 0, col = 0;
            for(int i=1; i<=capacity; i++) {
                final int sNo = i;
                Button sBtn = new Button(String.valueOf(i));
                sBtn.setPrefSize(40, 40);
                if (bookedSeats.contains(i)) {
                    sBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                    sBtn.setDisable(true);
                } else {
                    sBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                    sBtn.setOnAction(e -> {
                        if (lastSelectedBtn[0] != null) lastSelectedBtn[0].setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                        selectedSeatNum[0] = sNo;
                        lastSelectedBtn[0] = sBtn;
                        sBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                        msgLabel.setText("");
                    });
                }
                seatGrid.add(sBtn, col, row);

                col++;
                if (col == 2) {
                    Region aisle = new Region(); aisle.setPrefWidth(20);
                    seatGrid.add(aisle, col, row); col++;
                } else if (col > 4) { col = 0; row++; }
            }
            remainingLbl.setText("Remaining Seats: " + (capacity - bookedSeats.size()) + " / " + capacity);
        };

        busSelect.setOnAction(reloadAction);
        datePicker.setOnAction(reloadAction);
        timeSelect.setOnAction(reloadAction);

        // ── Payment & Confirm ──
        HBox payRow = new HBox(15);
        payRow.setAlignment(Pos.CENTER_LEFT);
        
        VBox payCol = new VBox(6);
        Label payLbl = new Label("Payment Method");
        payLbl.setStyle(fieldLabelStyle());
        ComboBox<String> paySelect = new ComboBox<>();
        paySelect.getItems().addAll("Credit/Debit Card", "eSewa", "Khalti", "Cash on Board");
        paySelect.setValue("Cash on Board");
        paySelect.setPrefWidth(200);
        paySelect.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 6; -fx-border-color: #e2e2e2; -fx-border-radius: 6; -fx-font-size: 13px;");
        payCol.getChildren().addAll(payLbl, paySelect);

        Button bookBtn = new Button("Confirm Booking  \u2192");
        bookBtn.setPrefHeight(42);
        bookBtn.setStyle("-fx-background-color: linear-gradient(to right, #0f3460, #16213e); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

        payRow.getChildren().addAll(payCol, bookBtn);

        bookBtn.setOnAction(e -> {
            if (busSelect.getValue() == null || datePicker.getValue() == null || timeSelect.getValue() == null) {
                msgLabel.setText("\u26A0 Please select Bus, Date, and Time."); msgLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); return;
            }
            // Validation: reject past dates (Test Case #1 fix)
            if (datePicker.getValue().isBefore(java.time.LocalDate.now())) {
                msgLabel.setText("\u26A0 Journey date cannot be in the past.");
                msgLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); return;
            }
            if (selectedSeatNum[0] == -1) {
                msgLabel.setText("\u26A0 Please select an open seat."); msgLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); return;
            }
            
            int busId = Integer.parseInt(busSelect.getValue().split(" - ")[0]);

            
            com.safetrack.model.Ticket t = controller.book(loggedInUserId, busId, selectedSeatNum[0], 
                datePicker.getValue().toString(), timeSelect.getValue(), paySelect.getValue(), "PAID");
                
            if (t != null) {
                String successMsg = "\u2714 Booking confirmed! Seat " + selectedSeatNum[0] + " on " + datePicker.getValue().toString();
                msgLabel.setText(successMsg);
                msgLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

                // Explicit success alert for better user feedback
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Success");
                alert.setHeaderText(null);
                alert.setContentText("Your booking has been successfully completed!\n" +
                                   "Bus: " + busSelect.getValue().split(" - ")[1] + "\n" +
                                   "Seat: " + selectedSeatNum[0] + "\n" +
                                   "Date: " + datePicker.getValue().toString());
                alert.showAndWait();

                routeSelect.setValue(null); busSelect.getItems().clear(); busSelect.setValue(null); datePicker.setValue(null); timeSelect.setValue(null);

                seatGrid.getChildren().clear(); remainingLbl.setText("Remaining Seats: -");
                selectedSeatNum[0] = -1; lastSelectedBtn[0] = null;
            } else {
                msgLabel.setText("\u2718 Sorry, seat was just taken!"); msgLabel.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold;");
            }
        });

        formCard.getChildren().addAll(instruc, routeBox, topRow, seatBox, payRow, msgLabel);
        contentArea.getChildren().add(formCard);
    }

    // ── MY BOOKINGS ───────────────────────────────────────────────────────────
    private void loadMyBookingsPage() {
        contentArea.getChildren().addAll(
                sectionLabel("My Bookings"),
                wrapCard(buildMyBookingsTable(-1)));
    }

    private VBox buildMyBookingsTable(int limit) {
        VBox box = new VBox(0);
        box.getChildren().add(tableRow(true, "Booking ID", "Bus Name", "Route", "Date & Time", "Seat", "Payment", "Status", "Actions"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT tk.id, tk.bus_id, tk.seat, tk.journey_date, tk.journey_time, tk.payment_method, tk.ride_status, b.name as bus_name, r.source, r.destination " +
                    "FROM bookings tk " +
                    "JOIN buses b ON tk.bus_id = b.id " +
                    "LEFT JOIN routes r ON b.route_id = r.id " +
                    "WHERE tk.user_id = " + loggedInUserId +
                    " ORDER BY tk.id DESC" + (limit > 0 ? " LIMIT " + limit : "");
            ResultSet rs = conn.createStatement().executeQuery(sql);
            boolean alt = false;
            while (rs.next()) {
                String routeStr = rs.getString("source") != null ? (rs.getString("source") + " \u2192 " + rs.getString("destination")) : "-";
                // Adding colored label for status
                Label statusLbl = new Label(rs.getString("ride_status"));
                statusLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + 
                    (rs.getString("ride_status").equals("COMPLETED") ? "#27ae60" : 
                    (rs.getString("ride_status").equals("CANCELLED") ? "#e74c3c" : "#f39c12")) + ";");
                
                HBox row = tableRow(false,
                        rs.getString("id"),
                        rs.getString("bus_name"),
                        routeStr,
                        rs.getString("journey_date") + " " + rs.getString("journey_time"),
                        rs.getString("seat"),
                        rs.getString("payment_method"),
                        statusLbl,
                        passengerActionBtns(rs.getString("id"), rs.getString("ride_status")));
                if (alt) row.setStyle(row.getStyle() + "-fx-background-color: #fafafa;");
                box.getChildren().add(row);
                alt = !alt;
            }
            if (box.getChildren().size() == 1)
                box.getChildren().add(emptyLabel("No bookings yet. Book your first ride!"));
        } catch (Exception ex) {
            box.getChildren().add(errorLabel(ex.getMessage()));
        }
        return box;
    }

    private HBox passengerActionBtns(String bookingId, String rideStatus) {
        HBox box = new HBox(8);
        if ("UPCOMING".equals(rideStatus)) {
            Button delBtn = new Button("✖ Cancel Ride");
            delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 4;");
            delBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this ride?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    new com.safetrack.dao.TicketDAO().updateRideStatus(Integer.parseInt(bookingId), "CANCELLED");
                    contentArea.getChildren().clear();
                    if(activeMenu.equals("Home")) loadHomePage();
                    else loadMyBookingsPage();
                }
            });
            box.getChildren().add(delBtn);
        } else {
            Label l = new Label("-");
            l.setStyle("-fx-text-fill: #999;");
            box.getChildren().add(l);
        }
        return box;
    }

    private void showPassengerEditBookingDialog(String bookingId, String busId, String oldSeat) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Booking Seat");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label instr = new Label("Select a new seat for your current bus schedule:");
        instr.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(8);
        seatGrid.setVgap(8);

        final int[] newSeat = {Integer.parseInt(oldSeat)};

        int capacity = 0;
        java.util.Set<Integer> bookedSeats = new java.util.HashSet<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rsCap = c.createStatement().executeQuery("SELECT capacity FROM buses WHERE id = " + busId);
            if(rsCap.next()) capacity = rsCap.getInt("capacity");

            ResultSet rsBook = c.createStatement().executeQuery("SELECT seat, id FROM bookings WHERE bus_id = " + busId);
            while(rsBook.next()) {
                if(rsBook.getInt("id") != Integer.parseInt(bookingId)) {
                    bookedSeats.add(rsBook.getInt("seat"));
                }
            }
        } catch(Exception ignored){}

        int row = 0;
        int col = 0;
        for(int i=1; i<=capacity; i++) {
            Button sBtn = new Button(String.valueOf(i));
            sBtn.setPrefSize(38, 38);
            final int sNo = i;
            if (bookedSeats.contains(i)) {
                sBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                sBtn.setDisable(true);
            } else {
                if (i == newSeat[0]) {
                    sBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    sBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                }
                sBtn.setOnAction(e -> {
                    newSeat[0] = sNo;
                    for(javafx.scene.Node n : seatGrid.getChildren()) {
                        if(n instanceof Button && !n.isDisabled()) n.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                    }
                    sBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                });
            }
            seatGrid.add(sBtn, col, row);

            col++;
            if (col == 2) {
                Region aisle = new Region();
                aisle.setPrefWidth(20);
                seatGrid.add(aisle, col, row);
                col++;
            } else if (col > 4) {
                col = 0;
                row++;
            }
        }

        content.getChildren().addAll(instr, seatGrid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try (Connection c = DatabaseConnection.getConnection()) {
                    PreparedStatement ps = c.prepareStatement("UPDATE bookings SET seat = ? WHERE id = ?");
                    ps.setInt(1, newSeat[0]);
                    ps.setInt(2, Integer.parseInt(bookingId));
                    ps.executeUpdate();
                    contentArea.getChildren().clear();
                    loadMyBookingsPage();
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    // ── EMERGENCY PAGE ────────────────────────────────────────────────────────
    private void loadEmergencyPage() {
        contentArea.getChildren().add(sectionLabel("Emergency Contacts"));

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: transparent;");

        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM emergency_contacts ORDER BY id");
            while (rs.next()) {
                String nameStr = rs.getString("name");
                String iconStr = rs.getString("relation") != null && !rs.getString("relation").isEmpty()
                        ? rs.getString("relation") : "📞";

                HBox card = new HBox(16);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(16, 20, 16, 20));
                // Applying the requested light-white card background and subtle border to match original image explicitly
                card.setStyle("-fx-background-color: #fcfcfc; -fx-background-radius: 8; -fx-border-color: #f1f3f5; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 2);");

                Label ic = new Label(iconStr);
                ic.setStyle("-fx-font-size: 26px;");

                VBox info = new VBox(3);
                Label nameLbl = new Label(nameStr);
                nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");
                Label numLbl = new Label(rs.getString("phone"));
                numLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
                info.getChildren().addAll(nameLbl, numLbl);

                card.getChildren().addAll(ic, info);
                box.getChildren().add(card);
            }
            if (box.getChildren().isEmpty()) box.getChildren().add(emptyLabel("No emergency contacts found."));
        } catch (Exception ex) { box.getChildren().add(errorLabel(ex.getMessage())); }

        contentArea.getChildren().add(box);
    }

    // ── SHARED HELPERS ────────────────────────────────────────────────────────
    private HBox tableRow(boolean header, Object... cols) {
        HBox row = new HBox(0);
        row.setStyle(header
                ? "-fx-background-color: #0f3460; -fx-padding: 12 16;"
                : "-fx-padding: 11 16; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
        for (Object col : cols) {
            if (col instanceof javafx.scene.Node) {
                javafx.scene.Node n = (javafx.scene.Node) col;
                if (n instanceof Region) {
                    ((Region) n).setPrefWidth(200);
                    ((Region) n).setMinWidth(50);
                    ((Region) n).setMaxWidth(Double.MAX_VALUE);
                }
                HBox.setHgrow(n, Priority.ALWAYS);
                row.getChildren().add(n);
            } else {
                Label lbl = new Label(col != null ? col.toString() : "-");
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

    private String fieldLabelStyle() {
        return "-fx-font-size: 12px; -fx-font-weight: bold; " +
                "-fx-text-fill: #555; -fx-font-family: 'Segoe UI';";
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

    public static void main(String[] args) { launch(args); }
}

