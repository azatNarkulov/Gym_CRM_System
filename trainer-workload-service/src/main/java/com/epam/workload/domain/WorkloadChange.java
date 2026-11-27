package com.epam.workload.domain;

import java.time.Duration;
import java.time.LocalDate;

public record WorkloadChange(
        String trainerUsername, String trainerFirstName, String trainerLastName, boolean active,
        LocalDate trainingDate, Duration trainingDuration, Action action) {
    public enum Action { ADD, DELETE }
}
