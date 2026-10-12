package com.epam.service;

import com.epam.domain.User;

import java.util.Optional;

public interface UserService {

    User add(User user);

    Optional<User> get(Long id);

    User update(String username, String password, User user);

    User updateInternal(User user);

    User findByUsername(String username, String password);

    User findByUsernameInternal(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username, String password);

    void deactivate(String username, String password);

    boolean matchCredentials(String username, String password);

    boolean matchPasswordOnly(String username, String password);

    void checkCredentials(String username, String password);

    String generatePassword();

    String generateUsername(User user);
}
