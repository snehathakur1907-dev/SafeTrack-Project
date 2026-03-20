package com.safetrack.view;

import com.safetrack.controller.BookingController;
import com.safetrack.model.Ticket;
import com.safetrack.util.SessionManager;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BookingView extends Application {

    private int selectedSeat = -1;

    @Override
    public void start(Stage stage) {

        BookingController controller = new BookingController();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        root.setStyle("-fx-background-color: linear-gradient(to right, #141E30, #243B55);");

        Label title = new Label("SELECT YOUR SEAT");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        // Bus selection
        ComboBox<String> busBox = new ComboBox<>();
        busBox.getItems().addAll("Bus 1", "Bus 2");

        // Seat grid
        GridPane seatsGrid = new GridPane();
        seatsGrid.setHgap(10);
        seatsGrid.setVgap(10);
        seatsGrid.setAlignment(Pos.CENTER);

        Button[][] seatButtons = new Button[4][5];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {

                int seatNum = i * 5 + j + 1;

                Button seat = new Button(String.valueOf(seatNum));
                seat.setPrefSize(50, 50);

                seat.setStyle("-fx-background-color: #00c6ff; -fx-text-fill: white;");

                seat.setOnAction(e -> {
                    selectedSeat = seatNum;
                    seat.setStyle("-fx-background-color: green;");
                });

                seatButtons[i][j] = seat;
                seatsGrid.add(seat, j, i);
            }
        }

        Button bookBtn = new Button("Book Ticket");
        Label message = new Label();
        message.setStyle("-fx-text-fill: white;");

        bookBtn.setOnAction(e -> {

            if (selectedSeat == -1) {
                message.setText("Please select a seat!");
                return;
            }

            int userId = SessionManager.getUser().getId();
            int busId = 1;

            Ticket t = controller.book(userId, busId, selectedSeat);

            if (t == null) {
                message.setText("Seat already booked!");
            } else {
                message.setText("Booking Successful!");
            }
        });

        root.getChildren().addAll(title, busBox, seatsGrid, bookBtn, message);

        Scene scene = new Scene(root, 500, 600);
        stage.setTitle("Booking");
        stage.setScene(scene);
        stage.show();
    }
}