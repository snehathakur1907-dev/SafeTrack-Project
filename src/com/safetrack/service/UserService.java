package com.safetrack.service;

import com.safetrack.dao.UserDAO;
import com.safetrack.model.User;

public class UserService {

    private UserDAO dao = new UserDAO();

    // Handles login logic + validation
    public User loginUser(String email, String password) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        return dao.login(email, password);
    }
}