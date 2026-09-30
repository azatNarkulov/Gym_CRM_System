package com.epam.exception;

public class StorageInitializerException extends RuntimeException {
    public StorageInitializerException(String message, Throwable cause) {
        super(message, cause);
    }
}
