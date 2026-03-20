package com.safetrack.model;

public class Bus {
    private int id;
    private String route;
    private int seatCapacity;

    public Bus(int id, String route, int seatCapacity) {
        this.id = id;
        this.route = route;
        this.seatCapacity = seatCapacity;
    }

    public int getId() { return id; }
    public String getRoute() { return route; }
    public int getSeatCapacity() { return seatCapacity; }
}
