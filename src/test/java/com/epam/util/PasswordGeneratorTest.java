package com.epam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PasswordGeneratorTest {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    public void shouldGeneratePasswordWithCorrectLength() {
        String password = passwordGenerator.generate();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    public void shouldGenerateDifferentPasswords() {
        String firstPassword = passwordGenerator.generate();
        String secondPassword = passwordGenerator.generate();

        assertNotEquals(firstPassword, secondPassword);
    }
}
