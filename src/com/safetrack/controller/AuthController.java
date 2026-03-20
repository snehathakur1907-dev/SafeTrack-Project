package com.safetrack.controller;

import com.safetrack.dao.UserDAO;
import com.safetrack.model.User;

public class AuthController {

    private UserDAO dao = new UserDAO();

    public User login(String email, String password) {
        return dao.login(email, password);
    }
}
