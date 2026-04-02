package com.safetrack.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * Shared UI helpers for Login and Register screens.
 * Extracted to eliminate ~120 lines of duplicated code between LoginView and RegisterView.
 * Used via composition (call AuthViewBase.buildLeftPanel() etc.) rather than inheritance,
 * to avoid Java's single-inheritance constraint with Application.
 */
public class AuthViewBase {

    /**
     * Builds the dark branded left panel used by both Login and Register screens.
     *
     * @param icon       emoji icon (e.g. "\uD83D\uDE96")
     * @param title      main title text
     * @param subtitle   italic subtitle
     * @param panelWidth width of the panel as a double string
     * @param badges     badge lines to display (up to 3)
     * @return a fully styled StackPane panel
     */
    public StackPane buildLeftPanel(String icon, String title,
                                    String subtitle, String panelWidth,
                                    String... badges) {
        double width = Double.parseDouble(panelWidth);
        StackPane pane = new StackPane();
        pane.setPrefWidth(width);

        Rectangle bg = new Rectangle(width, 640);
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

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 60px;");
        iconLbl.setEffect(new DropShadow(26, Color.web("#e94560", 0.7)));

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; " +
                "-fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(233,69,96,0.45);");
        sep.setPrefWidth(170);

        Label subLbl = new Label(subtitle);
        subLbl.setWrapText(true);
        subLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.55); " +
                "-fx-font-family: 'Segoe UI'; -fx-font-style: italic;");

        VBox badgeBox = new VBox(9);
        badgeBox.setAlignment(Pos.CENTER_LEFT);
        badgeBox.setPadding(new Insets(16, 0, 0, 16));
        for (String b : badges) badgeBox.getChildren().add(badge("\u2714", b));

        content.getChildren().addAll(iconLbl, titleLbl, sep, subLbl, badgeBox);

        Label copy = new Label("\u00A9 2025 SafeTrack Inc.");
        copy.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.20); " +
                "-fx-font-family: 'Segoe UI';");
        StackPane.setAlignment(copy, Pos.BOTTOM_CENTER);
        StackPane.setMargin(copy, new Insets(0, 0, 16, 0));

        pane.getChildren().addAll(bg, c1, c2, c3, content, copy);

        TranslateTransition tt = new TranslateTransition(Duration.millis(650), pane);
        tt.setFromX(-width); tt.setToX(0);
        tt.play();
        return pane;
    }

    /** Creates a labelled text or password field with focus styling. */
    public VBox makeField(String label, String prompt, boolean isPassword) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; " +
                "-fx-text-fill: #555; -fx-font-family: 'Segoe UI';");
        if (isPassword) {
            PasswordField pf = new PasswordField();
            pf.setPromptText(prompt); pf.setPrefHeight(40); pf.setStyle(fieldStyle());
            pf.focusedProperty().addListener((o, ov, nv) ->
                    pf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, pf);
        } else {
            TextField tf = new TextField();
            tf.setPromptText(prompt); tf.setPrefHeight(40); tf.setStyle(fieldStyle());
            tf.focusedProperty().addListener((o, ov, nv) ->
                    tf.setStyle(nv ? fieldFocus() : fieldStyle()));
            box.getChildren().addAll(lbl, tf);
        }
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    public HBox badge(String check, String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label ck = new Label(check);
        ck.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label lb = new Label(text);
        lb.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        row.getChildren().addAll(ck, lb);
        return row;
    }

    public Circle makeCircle(double r, Color c) { Circle ci = new Circle(r); ci.setFill(c); return ci; }

    public String fieldStyle() {
        return "-fx-background-color: #f4f5f7; -fx-background-radius: 9; " +
                "-fx-border-color: #e2e2e2; -fx-border-radius: 9; -fx-border-width: 1.5; " +
                "-fx-padding: 8 14; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';";
    }

    public String fieldFocus() {
        return "-fx-background-color: #fff7f8; -fx-background-radius: 9; " +
                "-fx-border-color: #e94560; -fx-border-radius: 9; -fx-border-width: 2; " +
                "-fx-padding: 8 14; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';";
    }

    public String btnStyle() {
        return "-fx-background-color: linear-gradient(to right, #e94560, #c0392b); " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family: 'Segoe UI';";
    }

    public String btnHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #c0392b, #e94560); " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-family: 'Segoe UI';";
    }
}
