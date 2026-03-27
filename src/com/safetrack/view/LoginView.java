package com.safetrack.view;

import com.safetrack.controller.AuthController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Login screen for SafeTrack.
 * Uses AuthViewBase (composition) for shared UI helpers — no raw SQL here.
 * Authentication is fully delegated to AuthController → UserDAO.
 */
public class LoginView extends Application {

    // Shared UI helpers via composition (Step 5: remove duplication)
    private final AuthViewBase ui = new AuthViewBase();

    private TextField     usernameField;
    private PasswordField passwordField;
    private Label         messageLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("SafeTrack \u2013 Taxi Booking");
        stage.setWidth(960);
        stage.setHeight(640);
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setLeft(ui.buildLeftPanel(
                "\uD83D\uDE96", "SafeTrack", "Your journey, our priority.", "480",
                "Book rides instantly", "Track your journey live", "Safe & secure travel"));
        root.setCenter(buildRightPanel(stage));

        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox buildRightPanel(Stage stage) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(44, 52, 44, 52));
        card.setMaxWidth(430);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        card.setEffect(new DropShadow(26, 0, 7, Color.gray(0, 0.10)));

        Label title = new Label("Welcome Back");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e; -fx-font-family: 'Segoe UI';");
        Label subtitle = new Label("Sign in to your account to continue");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-font-family: 'Segoe UI';");

        // Reuse makeField from AuthViewBase
        VBox userBox = ui.makeField("Username or Email", "Enter your username or email", false);
        usernameField = (TextField) userBox.getChildren().get(1);
        VBox passBox = ui.makeField("Password", "Enter your password", true);
        passwordField = (PasswordField) passBox.getChildren().get(1);

        messageLabel = new Label("");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

        Button loginBtn = new Button("Sign In  \u2192");
        loginBtn.setPrefWidth(Double.MAX_VALUE); loginBtn.setPrefHeight(44);
        loginBtn.setStyle(ui.btnStyle());
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(ui.btnHoverStyle()));
        loginBtn.setOnMouseExited(e  -> loginBtn.setStyle(ui.btnStyle()));
        loginBtn.setOnAction(e -> handleLogin(stage));
        usernameField.setOnAction(e -> handleLogin(stage));
        passwordField.setOnAction(e -> handleLogin(stage));

        HBox regRow = new HBox(5);
        regRow.setAlignment(Pos.CENTER);
        Label noAcc = new Label("Don't have an account?");
        noAcc.setStyle("-fx-font-size: 12px; -fx-text-fill: #777; -fx-font-family: 'Segoe UI';");
        Hyperlink regLink = new Hyperlink("Register now");
        regLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #e94560; -fx-font-weight: bold; " +
                "-fx-border-color: transparent; -fx-font-family: 'Segoe UI';");
        regLink.setOnAction(e -> {
            try { new RegisterView().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
        });
        regRow.getChildren().addAll(noAcc, regLink);

        card.getChildren().addAll(title, subtitle, userBox, passBox, messageLabel, loginBtn, regRow);

        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #f0f2f5;");
        wrapper.setPadding(new Insets(30, 40, 30, 40));
        wrapper.getChildren().add(card);

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(350)); ft.play();

        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    // ── LOGIN LOGIC — zero raw SQL, delegates entirely to AuthController ───────
    private void handleLogin(Stage stage) {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showMsg("\u26A0  Please fill in all fields.", "#e67e22"); return;
        }

        try {
            AuthController authController = new AuthController();
            int[] result = authController.loginAndGetIdRole(user, pass);

            if (result != null) {
                showMsg("\u2714  Login successful! Redirecting...", "#27ae60");
                if (result[1] == 1) new AdminDashboardView(result[0]).start(stage);
                else                new PassengerDashboardView(result[0]).start(stage);
            } else {
                showMsg("\u2718  Invalid username or password.", "#e94560");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMsg("\u26A0  Connection error: " + ex.getMessage(), "#e67e22");
        }
    }

    private void showMsg(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; -fx-text-fill: " + color + ";");
    }

    public static void main(String[] args) { launch(args); }
}