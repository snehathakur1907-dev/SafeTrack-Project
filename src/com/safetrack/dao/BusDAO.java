package com.safetrack.dao;

import com.safetrack.model.Bus;
import java.util.*;

public class BusDAO {

    private List<Bus> buses = new ArrayList<>();

    public BusDAO() {
        buses.add(new Bus(1, "Kathmandu-Pokhara", 40));
        buses.add(new Bus(2, "Kathmandu-Chitwan", 30));
    }

    public List<Bus> getBuses() {
        return buses;
    }
}
