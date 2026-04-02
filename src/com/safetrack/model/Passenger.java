package com.safetrack.model;

public class Passenger extends User {

    public Passenger(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "PASSENGER";
    }
}
