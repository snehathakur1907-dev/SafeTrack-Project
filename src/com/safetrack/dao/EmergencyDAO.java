package com.safetrack.dao;

import com.safetrack.model.EmergencyAlert;
import java.util.*;

public class EmergencyDAO {

    private List<EmergencyAlert> alerts = new ArrayList<>();

    public void saveAlert(EmergencyAlert alert) {
        alerts.add(alert);
        System.out.println("Emergency alert saved!");
    }
}
