package com.safetrack.model;

/**
 * Represents an emergency alert triggered by a passenger.
 * Stored in the alert_log table via EmergencyDAO.
 */
public class EmergencyAlert {

    private int    userId;
    private String message;

    public EmergencyAlert(int userId, String message) {
        this.userId  = userId;
        this.message = message;
    }

    // Getters — required for EmergencyDAO to read these fields
    public int    getUserId()   { return userId; }
    public String getMessage()  { return message; }
}