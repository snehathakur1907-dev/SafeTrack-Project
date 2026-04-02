package com.safetrack.model;

public class Ticket {
    private int ticketId;
    private int userId;
    private int busId;
    private int seatNumber;
    private String journeyDate;
    private String journeyTime;
    private String paymentMethod;
    private String paymentStatus;
    private String rideStatus;

    public Ticket(int ticketId, int userId, int busId, int seatNumber, String journeyDate, String journeyTime, String paymentMethod, String paymentStatus, String rideStatus) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.busId = busId;
        this.seatNumber = seatNumber;
        this.journeyDate = journeyDate;
        this.journeyTime = journeyTime;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.rideStatus = rideStatus;
    }

    public int getTicketId() { return ticketId; }
    public int getUserId() { return userId; }
    public int getBusId() { return busId; }
    public int getSeatNumber() { return seatNumber; }
    public String getJourneyDate() { return journeyDate; }
    public String getJourneyTime() { return journeyTime; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getRideStatus() { return rideStatus; }
}

