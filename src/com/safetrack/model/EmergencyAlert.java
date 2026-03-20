package com.safetrack.model;

public class EmergencyAlert {
    private int userId;
    private String message;

    public EmergencyAlert(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}
