package com.safetrack.model;

public class Ticket {
    private int ticketId;
    private int userId;
    private int busId;
    private int seatNumber;

    public Ticket(int ticketId, int userId, int busId, int seatNumber) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.busId = busId;
        this.seatNumber = seatNumber;
    }

    public int getTicketId() { return ticketId; }
    public int getUserId() { return userId; }
    public int getBusId() { return busId; }
    public int getSeatNumber() { return seatNumber; }
}
