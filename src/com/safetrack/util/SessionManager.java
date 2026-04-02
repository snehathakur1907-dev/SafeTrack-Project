package com.safetrack.util;

import com.safetrack.model.User;

/**
 * Manages the currently authenticated user for the session.
 * Use clearSession() on logout to prevent session leakage.
 */
public class SessionManager {

    private static User currentUser;

    /** Stores the authenticated user at login time. */
    public static void setUser(User user) { currentUser = user; }

    /** Returns the currently logged-in user, or null if not logged in. */
    public static User getUser() { return currentUser; }

    /** Clears the session — must be called on logout. */
    public static void clearSession() { currentUser = null; }
}
