package com.safetrack.model;

/**
 * Abstract base class representing any system user.
 * Subclasses (Admin, Passenger) implement getRole() via polymorphism.
 */
public abstract class User {

    // Private fields — accessed only through getters (encapsulation)
    private int id;
    private String name;
    private String email;
    private String password;

    public User(int id, String name, String email, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // Getters
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    /**
     * Returns the role of this user.
     * Overridden by Admin ("ADMIN") and Passenger ("PASSENGER").
     */
    public abstract String getRole();
}