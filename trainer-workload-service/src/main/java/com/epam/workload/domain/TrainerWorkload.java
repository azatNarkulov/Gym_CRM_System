package com.epam.workload.domain;

import java.time.Duration;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class TrainerWorkload {
    private final String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private final Map<YearMonth, Duration> months = new HashMap<>();

    public TrainerWorkload(String username, String firstName, String lastName, boolean active) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public void updateProfile(String firstName, String lastName, boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public Duration duration(YearMonth month) {
        return months.getOrDefault(month, Duration.ZERO);
    }

    public void setDuration(YearMonth month, Duration duration) {
        months.put(month, duration);
    }

    public String username() { return username; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public boolean active() { return active; }
}
