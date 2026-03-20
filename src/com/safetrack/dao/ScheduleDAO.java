package com.safetrack.dao;

import com.safetrack.model.Schedule;
import java.util.*;

public class ScheduleDAO {

    private List<Schedule> schedules = new ArrayList<>();

    public ScheduleDAO() {
        schedules.add(new Schedule(1, "10:00 AM", "4:00 PM"));
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }
}
