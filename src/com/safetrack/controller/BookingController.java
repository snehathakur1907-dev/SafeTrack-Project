package com.safetrack.controller;

import com.safetrack.dao.BusDAO;
import com.safetrack.dao.TicketDAO;
import com.safetrack.model.Bus;
import com.safetrack.model.Ticket;

import java.util.List;

/**
 * Controller for booking operations.
 * Delegates to BusDAO and TicketDAO — no SQL lives here.
 */
public class BookingController {

    private final BusDAO    busDAO    = new BusDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    /**
     * Returns all available buses from the database.
     */
    public List<Bus> getAvailableBuses() {
        return busDAO.getBuses();
    }

    /**
     * Books a seat and returns the created Ticket.
     * Returns null if the seat is already taken.
     */
    public Ticket book(int userId, int busId, int seat, String date, String time, String payAction, String payStatus) {
        return ticketDAO.bookTicket(userId, busId, seat, date, time, payAction, payStatus);
    }

    /**
     * Returns all tickets booked by a specific user.
     */
    public List<Ticket> getMyTickets(int userId) {
        return ticketDAO.getTicketsByUser(userId);
    }

    /**
     * Returns a set of already booked seat numbers for a bus on a specific date and time.
     */
    public java.util.Set<Integer> getBookedSeats(int busId, String date, String time) {
        return ticketDAO.getBookedSeats(busId, date, time);
    }
}