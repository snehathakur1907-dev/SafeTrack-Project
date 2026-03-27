package com.safetrack.view;

import com.safetrack.util.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

/**
 * Registration screen for SafeTrack.
 * Uses AuthViewBase (composition) for shared UI helpers — eliminates ~60 duplicate lines.
 */
public class RegisterView extends Application {

    // Shared UI helpers via composition (Step 5: remove duplication)
    private final AuthViewBase ui = new AuthViewBase();

    private TextField     nameField;
    private TextField     usernameField;
    private TextField     emailField;
    private PasswordField passwordField;
    private PasswordField confirmPassField;
    private ComboBox<String> roleBox;
    private Label         messageLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("SafeTrack \u2013 Create Account");
        stage.setWidth(960); stage.setHeight(640); stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setLeft(ui.buildLeftPanel(
                "\uD83D\uDD11", "Join SafeTrack", "Create your account in seconds.", "350",
                "Book rides instantly", "Track your driver live", "Safe & secure payments"));
        root.setCenter(buildRightPanel(stage));

        stage.setScene(new Scene(root));
        stage.show();
    }

    private ScrollPane buildRightPanel(Stage stage) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36, 44, 36, 44)); card.setMaxWidth(430);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        card.setEffect(new DropShadow(26, 0, 7, Color.gray(0, 0.10)));

        Label title    = new Label("Create Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");
        Label subtitle = new Label("Fill in the details below to get started");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-font-family: 'Segoe UI';");

        // Use ui.makeField() from AuthViewBase — replaces duplicated makeField() method
        HBox row1 = new HBox(12);
        VBox nameBox = ui.makeField("Full Name", "John Doe", false);
        nameField = (TextField) nameBox.getChildren().get(1);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        VBox userBox = ui.makeField("Username", "john_doe", false);
        usernameField = (TextField) userBox.getChildren().get(1);
        HBox.setHgrow(userBox, Priority.ALWAYS);
        row1.getChildren().addAll(nameBox, userBox);

        VBox emailBox = ui.makeField("Email Address", "john@example.com", false);
        emailField = (TextField) emailBox.getChildren().get(1);

        HBox row2 = new HBox(12);
        VBox passBox = ui.makeField("Password", "Min. 6 characters", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);
        HBox.setHgrow(passBox, Priority.ALWAYS);
        VBox confBox = ui.makeField("Confirm Password", "Repeat password", true);
        confirmPassField = (PasswordField) confBox.getChildren().get(1);
        HBox.setHgrow(confBox, Priority.ALWAYS);
        row2.getChildren().addAll(passBox, confBox);

        VBox roleGroup = new VBox(6);
        Label roleLbl = new Label("Account Type");
        roleLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #555; -fx-font-family: 'Segoe UI';");
        roleBox = new ComboBox<>();
        roleBox.getItems().add("PASSENGER"); roleBox.setValue("PASSENGER");
        roleBox.setPrefWidth(Double.MAX_VALUE); roleBox.setPrefHeight(40);
        roleBox.setStyle("-fx-background-color: #f4f5f7; -fx-border-color: #e2e2e2; " +
                "-fx-border-radius: 9; -fx-background-radius: 9; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        roleGroup.getChildren().addAll(roleLbl, roleBox);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        Button regBtn = new Button("Create Account  \u2192");
        regBtn.setPrefWidth(Double.MAX_VALUE); regBtn.setPrefHeight(44);
        regBtn.setStyle(ui.btnStyle());
        regBtn.setOnMouseEntered(e -> regBtn.setStyle(ui.btnHoverStyle()));
        regBtn.setOnMouseExited(e  -> regBtn.setStyle(ui.btnStyle()));
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

        card.getChildren().addAll(title, subtitle, row1, emailBox, row2, roleGroup, messageLabel, regBtn, loginRow);

        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #f0f2f5;");
        wrapper.setPadding(new Insets(30, 40, 30, 40));
        wrapper.getChildren().add(card);

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(350)); ft.play();

        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #f0f2f5; -fx-background-color: #f0f2f5;");
        return sp;
    }

    private void handleRegister(Stage stage) {
        String name  = nameField.getText().trim();
        String uname = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass  = passwordField.getText();
        String conf  = confirmPassField.getText();
        String role  = roleBox.getValue();

        if (name.isEmpty() || uname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showMsg("\u26A0  Please fill in all required fields.", "#e67e22"); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showMsg("\u26A0  Please enter a valid email address.", "#e67e22"); return;
        }
        if (pass.length() < 6) {
            showMsg("\u26A0  Password must be at least 6 characters.", "#e67e22"); return;
        }
        if (!pass.equals(conf)) {
            showMsg("\u2718  Passwords do not match.", "#e94560"); return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) { showMsg("\u26A0  DB connection failed.", "#e67e22"); return; }

            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ? OR username = ?");
            check.setString(1, email); check.setString(2, uname);
            if (check.executeQuery().next()) {
                showMsg("\u2718  Username or email already exists.", "#e94560"); return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name, username, email, password, role) VALUES (?,?,?,?,?)");
            ps.setString(1, name); ps.setString(2, uname);
            ps.setString(3, email); ps.setString(4, pass); ps.setString(5, role);
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
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; -fx-text-fill: " + color + ";");
    }

    private void openLogin(Stage stage) {
        try { new LoginView().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}
