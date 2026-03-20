package com.safetrack.view;

import com.safetrack.util.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class RegisterView extends Application {

    private TextField     nameField;
    private TextField     usernameField;
    private TextField     emailField;
    private PasswordField passwordField;
    private PasswordField confirmPassField;
    private ComboBox<String> roleBox;
    private Label         messageLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("SafeTrack – Create Account");
        stage.setWidth(960);
        stage.setHeight(640);
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setLeft(buildLeftPanel());
        root.setCenter(buildRightPanel(stage));

        stage.setScene(new Scene(root));
        stage.show();
    }

    // ── LEFT PANEL ────────────────────────────────────────────────────────────
    private StackPane buildLeftPanel() {
        StackPane pane = new StackPane();
        pane.setPrefWidth(350);

        Rectangle bg = new Rectangle(350, 640);
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#0f3460")),
                new Stop(0.5, Color.web("#16213e")),
                new Stop(1.0, Color.web("#1a1a2e"))));

        Circle c1 = makeCircle(120, Color.web("#e94560", 0.12));
        StackPane.setAlignment(c1, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(c1, new Insets(0, -45, -45, 0));

        Circle c2 = makeCircle(85, Color.web("#0f3460", 0.65));
        StackPane.setAlignment(c2, Pos.TOP_LEFT);
        StackPane.setMargin(c2, new Insets(-28, 0, 0, -28));

        Circle c3 = makeCircle(50, Color.web("#e94560", 0.07));
        StackPane.setAlignment(c3, Pos.CENTER);
        StackPane.setMargin(c3, new Insets(0, -75, 90, 0));

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(90, 26, 0, 26));

        Label icon = new Label("\uD83D\uDD11"); // 🔑
        icon.setStyle("-fx-font-size: 60px;");
        icon.setEffect(new DropShadow(26, Color.web("#e94560", 0.7)));

        Label title = new Label("Join SafeTrack");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; " +
                "-fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(233,69,96,0.45);");
        sep.setPrefWidth(170);

        Label sub = new Label("Create your account in seconds.");
        sub.setWrapText(true);
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.55); " +
                "-fx-font-family: 'Segoe UI'; -fx-font-style: italic;");

        VBox badges = new VBox(9);
        badges.setAlignment(Pos.CENTER_LEFT);
        badges.setPadding(new Insets(16, 0, 0, 16));
        badges.getChildren().addAll(
                badge("\u2714", "Book rides instantly"),
                badge("\u2714", "Track your driver live"),
                badge("\u2714", "Safe & secure payments")
        );

        content.getChildren().addAll(icon, title, sep, sub, badges);

        Label copy = new Label("\u00A9 2025 SafeTrack Inc.");
        copy.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.20); " +
                "-fx-font-family: 'Segoe UI';");
        StackPane.setAlignment(copy, Pos.BOTTOM_CENTER);
        StackPane.setMargin(copy, new Insets(0, 0, 16, 0));

        pane.getChildren().addAll(bg, c1, c2, c3, content, copy);

        TranslateTransition tt = new TranslateTransition(Duration.millis(650), pane);
        tt.setFromX(-350);
        tt.setToX(0);
        tt.play();

        return pane;
    }

    private HBox badge(String check, String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label ck = new Label(check);
        ck.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label lb = new Label(text);
        lb.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 12px; " +
                "-fx-font-family: 'Segoe UI';");
        row.getChildren().addAll(ck, lb);
        return row;
    }

    // ── RIGHT PANEL ───────────────────────────────────────────────────────────
    private ScrollPane buildRightPanel(Stage stage) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36, 44, 36, 44));
        card.setMaxWidth(430);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        card.setEffect(new DropShadow(26, 0, 7, Color.gray(0, 0.10)));

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; " +
                "-fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");

        Label subtitle = new Label("Fill in the details below to get started");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-font-family: 'Segoe UI';");

        // Row 1: Full Name + Username
        HBox row1 = new HBox(12);
        VBox nameBox = makeField("Full Name", "John Doe", false);
        nameField = (TextField) nameBox.getChildren().get(1);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        VBox userBox = makeField("Username", "john_doe", false);
        usernameField = (TextField) userBox.getChildren().get(1);
        HBox.setHgrow(userBox, Priority.ALWAYS);
        row1.getChildren().addAll(nameBox, userBox);

        // Email full width
        VBox emailBox = makeField("Email Address", "john@example.com", false);
        emailField = (TextField) emailBox.getChildren().get(1);

        // Row 2: Password + Confirm
        HBox row2 = new HBox(12);
        VBox passBox = makeField("Password", "Min. 6 characters", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);
        HBox.setHgrow(passBox, Priority.ALWAYS);

        VBox confBox = makeField("Confirm Password", "Repeat password", true);
        confirmPassField = (PasswordField) confBox.getChildren().get(1);
        HBox.setHgrow(confBox, Priority.ALWAYS);
        row2.getChildren().addAll(passBox, confBox);

        // Role
        VBox roleGroup = new VBox(6);
        Label roleLbl = new Label("Account Type");
        roleLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; " +
                "-fx-text-fill: #555; -fx-font-family: 'Segoe UI';");
        roleBox = new ComboBox<>();
        roleBox.getItems().add("PASSENGER");
        roleBox.setValue("PASSENGER");
//        roleBox.setDisable(true); // 🔥 disables editing
        roleBox.setPrefWidth(Double.MAX_VALUE);
        roleBox.setPrefHeight(40);
        roleBox.setStyle("-fx-background-color: #f4f5f7; -fx-border-color: #e2e2e2; " +
                "-fx-border-radius: 9; -fx-background-radius: 9; " +
                "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        roleGroup.getChildren().addAll(roleLbl, roleBox);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        Button regBtn = new Button("Create Account  \u2192");
        regBtn.setPrefWidth(Double.MAX_VALUE);
        regBtn.setPrefHeight(44);
        regBtn.setStyle(btnStyle());
        regBtn.setOnMouseEntered(e -> regBtn.setStyle(btnHoverStyle()));
        regBtn.setOnMouseExited(e  -> regBtn.setStyle(btnStyle()));
        regBtn.setOnAction(e -> handleRegister(stage));

        HBox loginRow = new HBox(5);
        loginRow.setAlignment(Pos.CENTER);
        Label already = new Label("Already have an account?");
        already.setStyle("-fx-font-size: 12px; -fx-text-fill: #777; -fx-font-family: 'Segoe UI';");
        Hyperlink loginLink = new Hyperlink("Sign in");
        loginLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #e94560; -fx-font-weight: bold; " +
                "-fx-border-color: transparent; -fx-font-family: 'Segoe UI';");
        loginLink.setOnAction(e -> openLogin(stage));
        loginRow.getChildren().addAll(already, loginLink);

        card.getChildren().addAll(title, subtitle, row1, emailBox, row2,
                roleGroup, messageLabel, regBtn, loginRow);

        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #f0f2f5;");
        wrapper.setPadding(new Insets(30, 40, 30, 40));
        wrapper.getChildren().add(card);

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(350));
        ft.play();

        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f0f2f5; -fx-background-color: #f0f2f5;");
        return sp;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private VBox makeField(String label, String prompt, boolean isPassword) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; " +
                "-fx-text-fill: #555; -fx-font-family: 'Segoe UI';");

        if (isPassword) {
            PasswordField pf = new PasswordField();
            pf.setPromptText(prompt);
            pf.setPrefHeight(40);
            pf.setStyle(fieldStyle());
            pf.focusedProperty().addListener((o, ov, nv) ->
                    pf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, pf);
        } else {
            TextField tf = new TextField();
            tf.setPromptText(prompt);
            tf.setPrefHeight(40);
            tf.setStyle(fieldStyle());
            tf.focusedProperty().addListener((o, ov, nv) ->
                    tf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, tf);
        }
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private String fieldStyle() {
        return "-fx-background-color: #f4f5f7; -fx-background-radius: 9; " +
                "-fx-border-color: #e2e2e2; -fx-border-radius: 9; -fx-border-width: 1.5; " +
                "-fx-padding: 8 14; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';";
    }

    private String fieldFocus() {
        return "-fx-background-color: #fff7f8; -fx-background-radius: 9; " +
                "-fx-border-color: #e94560; -fx-border-radius: 9; -fx-border-width: 2; " +
                "-fx-padding: 8 14; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';";
    }

    private String btnStyle() {
        return "-fx-background-color: linear-gradient(to right, #e94560, #c0392b); " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family: 'Segoe UI';";
    }

    private String btnHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #c0392b, #e94560); " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family: 'Segoe UI';";
    }

    private Circle makeCircle(double r, Color c) {
        Circle ci = new Circle(r);
        ci.setFill(c);
        return ci;
    }

    // ── REGISTER LOGIC ────────────────────────────────────────────────────────
    private void handleRegister(Stage stage) {
        String name  = nameField.getText().trim();
        String uname = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass  = passwordField.getText();
        String conf  = confirmPassField.getText();
        String role  = roleBox.getValue();
//        String role = "PASSENGER"; // 🔥 force role

        if (name.isEmpty() || uname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showMsg("\u26A0  Please fill in all required fields.", "#e67e22");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showMsg("\u26A0  Please enter a valid email address.", "#e67e22");
            return;
        }
        if (pass.length() < 6) {
            showMsg("\u26A0  Password must be at least 6 characters.", "#e67e22");
            return;
        }
        if (!pass.equals(conf)) {
            showMsg("\u2718  Passwords do not match.", "#e94560");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check for duplicates
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ? OR username = ?");
            check.setString(1, email);
            check.setString(2, uname);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                showMsg("\u2718  Username or email already exists.", "#e94560");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name, username, email, password, role) VALUES (?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, uname);
            ps.setString(3, email);
            ps.setString(4, pass);
            ps.setString(5, role);
            ps.executeUpdate();

            showMsg("\u2714  Account created! Redirecting to login...", "#27ae60");

            new Thread(() -> {
                try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> openLogin(stage));
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
            showMsg("\u26A0  DB error: " + ex.getMessage(), "#e67e22");
        }
    }

    private void showMsg(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; " +
                "-fx-text-fill: " + color + ";");
    }

    private void openLogin(Stage stage) {
        try { new LoginView().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}

