package com.epam.service;

import com.epam.domain.User;

import java.util.Optional;

public interface UserService<T extends User> {

    T add(T user);

    Optional<T> get(Long id);

    T update(T user);

    T findByUsername(String username);

    void changePassword(String username, String newPassword);

    void activate(String username);

    void deactivate(String username);

    boolean matchCredentials(String username, String password);
}
