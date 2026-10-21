package com.epam.service.impl;

import com.epam.domain.User;
import com.epam.exception.AuthenticationException;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.UserDao;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final Random random = new SecureRandom();

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void prepareForCreation(User user) {
        validateRequiredFields(user);
        user.setUsername(generateUsername(user));
        user.setPassword(generatePassword());
        user.setActive(true);
        LOG.info("Prepared credentials for user: {}", user.getUsername());
    }

    @Override
    public Optional<User> get(Long id) {
        LOG.debug("Getting user by id: {}", id);
        return Optional.ofNullable(userDao.get(id));
    }

    @Override
    public User update(User user) {
        validateRequiredFields(user);
        LOG.info("Updating user: {}", user.getUsername());

        Long id = user.getId();
        if (id == null) {
            throw new UserNotFoundException();
        }

        User existing = get(id).orElseThrow(UserNotFoundException::new);

        String newUsername = user.getUsername();
        if (newUsername != null && !newUsername.equals(existing.getUsername())) {
            throw new ValidationException("Username cannot be changed");
        }
        user.setUsername(existing.getUsername());
        user.setPassword(existing.getPassword());

        return userDao.update(user);
    }

    @Override
    public User findByUsername(String username) {
        LOG.debug("Finding user by username: {}", username);
        return userDao.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);
        if (!user.getPassword().equals(oldPassword)) {
            throw new AuthenticationException("Username or password is incorrect");
        }
        LOG.info("Changing password for user: {}", username);
        user.setPassword(newPassword);
        userDao.update(user);
    }

    @Override
    public void setActive(String username, boolean active) {
        User user = findByUsername(username);
        if (user.isActive() == active) {
            String status = active ? "active" : "inactive";
            throw new ValidationException("User is already " + status + ": " + username);
        }
        user.setActive(active);
        LOG.info("{} user: {}", active ? "Activating" : "Deactivating", username);
        userDao.update(user);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        LOG.debug("Matching username and password: {}", username);
        return userDao.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .filter(User::isActive)
                .isPresent();
    }

    @Override
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return password.toString();
    }

    @Override
    public String generateUsername(User user) {
        String baseUsername = user.getFirstName() + "." + user.getLastName();
        String username;

        if (!userDao.existsByUsername(baseUsername)) {
            username = baseUsername;
        } else {
            int serialNumber = 1;

            while (userDao.existsByUsername(baseUsername + serialNumber)) {
                serialNumber++;
            }

            username = baseUsername + serialNumber;
        }

        LOG.debug("Generated username: {}", username);
        return username;
    }

    private void validateRequiredFields(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }
}
