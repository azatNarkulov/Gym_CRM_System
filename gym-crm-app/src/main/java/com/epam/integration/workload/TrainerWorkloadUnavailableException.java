package com.epam.integration.workload;

public class TrainerWorkloadUnavailableException extends RuntimeException {

    public TrainerWorkloadUnavailableException(Throwable cause) {
        super("Trainer Workload service is unavailable", cause);
    }
}
