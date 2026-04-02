package com.safetrack.controller;

import com.safetrack.dao.UserDAO;

/**
 * Controller for user profile operations.
 * Views must use this class — no direct DAO/SQL in views.
 */
public class UserController {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Returns profile details for a user.
     * @param userId the user's primary key
     * @return String[] {name, username, email} or null if not found
     */
    public String[] getProfile(int userId) {
        return userDAO.getUserProfileDetails(userId);
    }

    /**
     * Updates a user's profile. Password is only updated if non-empty.
     * @param userId   primary key of the user to update
     * @param name     new display name
     * @param username new username
     * @param email    new email address
     * @param password new password (leave blank to keep existing)
     * @return true if the update was successful
     */
    public boolean updateProfile(int userId, String name, String username,
                                  String email, String password) {
        return userDAO.updateProfile(userId, name, username, email, password);
    }

    /**
     * Deletes a user by ID.
     * @param userId primary key of the user to delete
     */
    public void deleteUser(int userId) {
        userDAO.deleteUser(userId);
    }
}
