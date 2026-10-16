package com.epam.service;

import com.epam.domain.User;

import java.util.Optional;

public interface UserService {

    void prepareForCreation(User user);

    Optional<User> get(Long id);

    User update(User user);

    User findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void setActive(String username, boolean active);

    boolean matchCredentials(String username, String password);

    String generatePassword();

    String generateUsername(User user);
}
