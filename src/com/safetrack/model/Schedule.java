package com.safetrack.model;

public class Schedule {
    private int busId;
    private String departureTime;
    private String arrivalTime;

    public Schedule(int busId, String departureTime, String arrivalTime) {
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public int getBusId() { return busId; }
}
