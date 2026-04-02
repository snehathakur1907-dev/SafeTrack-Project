package com.safetrack.model;

/**
 * Represents a bus departure and arrival schedule.
 */
public class Schedule {

    private int    busId;
    private String departureTime;
    private String arrivalTime;

    public Schedule(int busId, String departureTime, String arrivalTime) {
        this.busId         = busId;
        this.departureTime = departureTime;
        this.arrivalTime   = arrivalTime;
    }

    // Getters
    public int    getBusId()        { return busId; }
    public String getDepartureTime(){ return departureTime; }
    public String getArrivalTime()  { return arrivalTime; }
}