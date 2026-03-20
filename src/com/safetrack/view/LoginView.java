package com.safetrack.view;

import com.safetrack.util.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class LoginView extends Application {

    private TextField     usernameField;
    private PasswordField passwordField;
    private Label         messageLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("SafeTrack – Taxi Booking");
        stage.setWidth(880);
        stage.setHeight(580);
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
        pane.setPrefWidth(370);

        Rectangle bg = new Rectangle(370, 580);
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#1a1a2e")),
                new Stop(0.5, Color.web("#16213e")),
                new Stop(1.0, Color.web("#0f3460"))));

        Circle c1 = makeCircle(150, Color.web("#e94560", 0.13));
        StackPane.setAlignment(c1, Pos.TOP_RIGHT);
        StackPane.setMargin(c1, new Insets(-60, -60, 0, 0));

        Circle c2 = makeCircle(110, Color.web("#0f3460", 0.7));
        StackPane.setAlignment(c2, Pos.BOTTOM_LEFT);
        StackPane.setMargin(c2, new Insets(0, 0, -40, -40));

        Circle c3 = makeCircle(70, Color.web("#e94560", 0.08));
        StackPane.setAlignment(c3, Pos.CENTER);
        StackPane.setMargin(c3, new Insets(100, 0, 0, -110));

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(80, 30, 0, 30));

        Label icon = new Label("\uD83D\uDE96"); // 🚖
        icon.setStyle("-fx-font-size: 68px;");
        icon.setEffect(new DropShadow(28, Color.web("#e94560", 0.75)));

        Label appName = new Label("SafeTrack");
        appName.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; " +
                "-fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(233,69,96,0.5);");
        sep.setPrefWidth(180);

        Label tagline = new Label("Your journey, our priority.");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.55); " +
                "-fx-font-family: 'Segoe UI'; -fx-font-style: italic;");
        tagline.setTextAlignment(TextAlignment.CENTER);

        Label dots = new Label("Fast  \u2022  Safe  \u2022  Reliable");
        dots.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.35); " +
                "-fx-font-family: 'Segoe UI';");

        content.getChildren().addAll(icon, appName, sep, tagline, dots);

        Label copy = new Label("\u00A9 2025 SafeTrack Inc.");
        copy.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.22); " +
                "-fx-font-family: 'Segoe UI';");
        StackPane.setAlignment(copy, Pos.BOTTOM_CENTER);
        StackPane.setMargin(copy, new Insets(0, 0, 16, 0));

        pane.getChildren().addAll(bg, c1, c2, c3, content, copy);

        TranslateTransition tt = new TranslateTransition(Duration.millis(650), pane);
        tt.setFromX(-370);
        tt.setToX(0);
        tt.play();

        return pane;
    }

    // ── RIGHT PANEL ───────────────────────────────────────────────────────────
    private VBox buildRightPanel(Stage stage) {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #f0f2f5;");
        wrapper.setPadding(new Insets(40, 60, 40, 60));

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(40, 44, 40, 44));
        card.setMaxWidth(360);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        card.setEffect(new DropShadow(28, 0, 8, Color.gray(0, 0.10)));

        Label title = new Label("Welcome Back");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; " +
                "-fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; " +
                "-fx-font-family: 'Segoe UI';");

        VBox userBox = makeField("Username or Email", false);
        usernameField = (TextField) userBox.getChildren().get(1);

        VBox passBox = makeField("Password", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        Button loginBtn = new Button("Login  \u2192");
        loginBtn.setPrefWidth(272);
        loginBtn.setPrefHeight(44);
        loginBtn.setStyle(btnStyle());
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(btnHoverStyle()));
        loginBtn.setOnMouseExited(e  -> loginBtn.setStyle(btnStyle()));
        loginBtn.setOnAction(e -> handleLogin(stage));
        usernameField.setOnAction(e -> handleLogin(stage));
        passwordField.setOnAction(e -> handleLogin(stage));

        // Divider
        HBox divider = new HBox(8);
        divider.setAlignment(Pos.CENTER);
        Region dl = new Region(); dl.setPrefHeight(1);
        dl.setStyle("-fx-background-color: #e8e8e8;");
        HBox.setHgrow(dl, Priority.ALWAYS);
        Region dr = new Region(); dr.setPrefHeight(1);
        dr.setStyle("-fx-background-color: #e8e8e8;");
        HBox.setHgrow(dr, Priority.ALWAYS);
        Label orLbl = new Label("or");
        orLbl.setStyle("-fx-text-fill: #bbb; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        divider.getChildren().addAll(dl, orLbl, dr);

        HBox regRow = new HBox(5);
        regRow.setAlignment(Pos.CENTER);
        Label noAcc = new Label("Don't have an account?");
        noAcc.setStyle("-fx-font-size: 12px; -fx-text-fill: #777; -fx-font-family: 'Segoe UI';");
        Hyperlink regLink = new Hyperlink("Register now");
        regLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #e94560; -fx-font-weight: bold; " +
                "-fx-border-color: transparent; -fx-font-family: 'Segoe UI';");
        regLink.setOnAction(e -> openRegister(stage));
        regRow.getChildren().addAll(noAcc, regLink);

        card.getChildren().addAll(title, subtitle, userBox, passBox,
                messageLabel, loginBtn, divider, regRow);

        wrapper.getChildren().add(card);

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(380));
        ft.play();

        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private VBox makeField(String label, boolean isPassword) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555; -fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold;");

        if (isPassword) {
            PasswordField pf = new PasswordField();
            pf.setPromptText("Enter your password");
            pf.setPrefHeight(40);
            pf.setStyle(fieldStyle());
            pf.focusedProperty().addListener((o, ov, nv) ->
                    pf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, pf);
        } else {
            TextField tf = new TextField();
            tf.setPromptText("Enter username or email");
            tf.setPrefHeight(40);
            tf.setStyle(fieldStyle());
            tf.focusedProperty().addListener((o, ov, nv) ->
                    tf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, tf);
        }
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

    // ── LOGIN LOGIC ───────────────────────────────────────────────────────────
    private void handleLogin(Stage stage) {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showMsg("\u26A0  Please fill in all fields.", "#e67e22");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, role FROM users WHERE (email = ? OR username = ?) AND password = ?");
            ps.setString(1, user);
            ps.setString(2, user);
            ps.setString(3, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int currentUserId = rs.getInt("id");
                String role = rs.getString("role");
                showMsg("\u2714  Login successful! Redirecting...", "#27ae60");
                if ("ADMIN".equalsIgnoreCase(role)) {
                    new AdminDashboardView(currentUserId).start(stage);
                } else {
                    new PassengerDashboardView(currentUserId).start(stage);
                }
            } else {
                showMsg("\u2718  Invalid username or password.", "#e94560");
                shake(passwordField);
            }
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

    private void shake(Control c) {
        TranslateTransition t = new TranslateTransition(Duration.millis(55), c);
        t.setFromX(0); t.setByX(9);
        t.setCycleCount(6); t.setAutoReverse(true);
        t.play();
    }

    private void openRegister(Stage stage) {
        try { new RegisterView().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}

