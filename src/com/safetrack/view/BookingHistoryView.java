package com.safetrack.view;

import com.safetrack.dao.TicketDAO;
import com.safetrack.model.Ticket;
import com.safetrack.util.SessionManager;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BookingHistoryView extends Application {

    private TicketDAO dao = new TicketDAO();

    @Override
    public void start(Stage stage) {

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        root.setStyle("-fx-background-color: linear-gradient(to right, #141E30, #243B55);");

        Label title = new Label("MY BOOKINGS");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        TableView<Ticket> table = new TableView<>();

        TableColumn<Ticket, Integer> idCol = new TableColumn<>("Ticket ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTicketId()).asObject());

        TableColumn<Ticket, Integer> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getSeatNumber()).asObject());

        table.getColumns().addAll(idCol, seatCol);

        int userId = SessionManager.getUser().getId();
        ObservableList<Ticket> data = FXCollections.observableArrayList(dao.getTicketsByUser(userId));
        table.setItems(data);

        Button cancelBtn = new Button("Cancel Ticket");

        cancelBtn.setOnAction(e -> {
            Ticket selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                dao.cancelTicket(selected.getTicketId());
                data.remove(selected);
            }
        });

        root.getChildren().addAll(title, table, cancelBtn);

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Booking History");
        stage.setScene(scene);
        stage.show();
    }
}
