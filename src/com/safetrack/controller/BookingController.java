package com.safetrack.controller;

import com.safetrack.dao.*;
import com.safetrack.model.*;

public class BookingController {

    private BusDAO busDAO = new BusDAO();
    private TicketDAO ticketDAO = new TicketDAO();

    public void showBuses() {
        for (Bus b : busDAO.getBuses()) {
            System.out.println(b.getId() + " - " + b.getRoute());
        }
    }

    public Ticket book(int userId, int busId, int seat) {
        return ticketDAO.bookTicket(userId, busId, seat);
    }
}
