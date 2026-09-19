package com.epam.object;

import lombok.Data;

import java.security.SecureRandom;
import java.util.Random;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;

    private final Random random = new SecureRandom();

    public void setGeneratedPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder password = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        this.password = password.toString();
    }
}
