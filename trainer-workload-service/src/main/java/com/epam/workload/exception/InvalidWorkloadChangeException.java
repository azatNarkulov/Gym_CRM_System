package com.epam.workload.exception;

public class InvalidWorkloadChangeException extends RuntimeException {

    public InvalidWorkloadChangeException() {
        super("Cannot remove more workload than is currently recorded");
    }
}
