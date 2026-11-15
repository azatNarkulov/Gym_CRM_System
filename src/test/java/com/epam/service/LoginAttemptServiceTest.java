package com.epam.service;

import com.epam.exception.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    public void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    public void shouldBlockAfterThreeFailures() {
        service.loginFailed("user");
        service.loginFailed("user");
        service.loginFailed("user");

        assertThrows(TooManyRequestsException.class, () -> service.checkBlocked("user"));
    }

    @Test
    public void shouldNotBlockAfterTwoFailures() {
        service.loginFailed("user");
        service.loginFailed("user");

        assertDoesNotThrow(() -> service.checkBlocked("user"));
    }

    @Test
    public void shouldResetCounterOnSuccess() {
        service.loginFailed("user");
        service.loginFailed("user");
        service.loginSucceeded("user");
        service.loginFailed("user");

        assertDoesNotThrow(() -> service.checkBlocked("user"));
    }
}
