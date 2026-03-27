package com.safetrack.controller;

import com.safetrack.dao.UserDAO;
import com.safetrack.model.User;
import com.safetrack.service.UserService;

/**
 * Controller for authentication operations.
 * Views must authenticate through this class — no direct SQL in views.
 */
public class AuthController {

    private final UserService service = new UserService();
    private final UserDAO    userDAO  = new UserDAO();

    /**
     * Authenticates via the service layer and returns a typed User model.
     * @param email    username or email
     * @param password plaintext password
     * @return User (Admin or Passenger), or null if invalid credentials
     */
    public User login(String email, String password) {
        return service.loginUser(email, password);
    }

    /**
     * Authenticates and returns a compact [id, roleCode] array for routing.
     * roleCode: 1 = ADMIN, 2 = PASSENGER. Returns null if invalid.
     * @param emailOrUsername username or email
     * @param password        plaintext password
     * @return int[]{id, roleCode} or null
     */
    public int[] loginAndGetIdRole(String emailOrUsername, String password) {
        return userDAO.loginAndGetIdRole(emailOrUsername, password);
    }
}