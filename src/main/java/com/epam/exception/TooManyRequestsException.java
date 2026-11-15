package com.epam.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Account is temporarily blocked. Try again later");
    }
}
