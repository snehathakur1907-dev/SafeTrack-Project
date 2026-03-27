package com.safetrack.controller;

import com.safetrack.dao.BusDAO;
import com.safetrack.dao.UserDAO;
import com.safetrack.model.Bus;
import com.safetrack.model.User;

import java.util.List;

/**
 * Controller for admin operations.
 * Acts as the intermediary between AdminDashboardView and the DAO layer,
 * keeping business logic out of the GUI code.
 */
public class AdminController {

    private final BusDAO  busDAO  = new BusDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Returns all buses in the system via BusDAO.
     */
    public List<Bus> getAllBuses() {
        return busDAO.getBuses();
    }

    /**
     * Looks up a user by ID — used by the admin panel to display user info.
     */
    public User getUserById(int id) {
        return userDAO.findById(id);
    }
}