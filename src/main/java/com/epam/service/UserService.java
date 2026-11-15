package com.epam.service;

import com.epam.domain.User;

import java.util.Optional;

public interface UserService {

    String prepareForCreation(User user);

    Optional<User> get(Long id);

    User update(User user);

    User findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void setActive(String username, boolean active);

    String generatePassword();

    String generateUsername(User user);
}
