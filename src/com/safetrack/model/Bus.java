package com.safetrack.model;

public class Bus {
    private int id;
    private String route;
    private int seatCapacity;
    private String departureTime;

    public Bus(int id, String route, int seatCapacity, String departureTime) {
        this.id = id;
        this.route = route;
        this.seatCapacity = seatCapacity;
        this.departureTime = departureTime;
    }

    public int getId() { return id; }
    public String getRoute() { return route; }
    public int getSeatCapacity() { return seatCapacity; }
    public String getDepartureTime() { return departureTime; }
}
