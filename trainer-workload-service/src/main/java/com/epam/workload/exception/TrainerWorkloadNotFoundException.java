package com.epam.workload.exception;

public class TrainerWorkloadNotFoundException extends RuntimeException {

    public TrainerWorkloadNotFoundException(String username) {
        super("Workload not found for trainer: " + username);
    }
}
