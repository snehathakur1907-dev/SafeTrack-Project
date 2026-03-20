package com.safetrack.controller;

import com.safetrack.dao.EmergencyDAO;
import com.safetrack.model.EmergencyAlert;

public class EmergencyController {

    private EmergencyDAO dao = new EmergencyDAO();

    public void trigger(int userId) {
        dao.saveAlert(new EmergencyAlert(userId, "Emergency!"));
    }
}
