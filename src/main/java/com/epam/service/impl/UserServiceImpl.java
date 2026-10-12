package com.epam.service.impl;

import com.epam.domain.User;
import com.epam.exception.AuthenticationException;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.UserDao;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserDao userDao;

    @Override
    public User add(User user) {
        validateRequiredFields(user);
        LOG.info("Creating user: {} {}", user.getFirstName(), user.getLastName());

        user.setUsername(generateUsername(user));
        user.setPassword(generatePassword());

        return userDao.add(user);
    }

    @Override
    public Optional<User> get(Long id) {
        LOG.debug("Getting user by id: {}", id);
        return Optional.ofNullable(userDao.get(id));
    }

    @Override
    public User update(String username, String password, User user) {
        checkCredentials(username, password);
        return updateInternal(user);
    }

    @Override
    public User updateInternal(User user) {
        validateRequiredFields(user);
        LOG.info("Updating user: {}", user.getUsername());

        Long id = user.getId();
        if (id == null) {
            throw new UserNotFoundException();
        }

        User existing = get(id).orElseThrow(UserNotFoundException::new);

        String newUsername = user.getUsername();
        if (newUsername == null) {
            user.setUsername(existing.getUsername());
        } else if (!newUsername.equals(existing.getUsername()) && userDao.existsByUsername(newUsername)) {
            throw new ValidationException("Username already taken: " + newUsername);
        }

        user.setActive(existing.isActive());
        user.setPassword(existing.getPassword());

        return userDao.update(user);
    }

    @Override
    public User findByUsername(String username, String password) {
        checkCredentials(username, password);
        return findByUsernameInternal(username);
    }

    @Override
    public User findByUsernameInternal(String username) {
        LOG.debug("Finding user by username: {}", username);
        return userDao.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        checkCredentials(username, oldPassword);
        LOG.info("Changing password for user: {}", username);
        User user = findByUsernameInternal(username);
        user.setPassword(newPassword);
        userDao.update(user);
    }

    @Override
    public void activate(String username, String password) {
        if (!matchPasswordOnly(username, password)) {
            throw new AuthenticationException("Username or password is incorrect");
        }

        User user = findByUsernameInternal(username);
        if (user.isActive()) {
            throw new ValidationException("User is already active: " + username);
        }
        user.setActive(true);
        LOG.info("Activating user: {}", username);
        userDao.update(user);
    }

    @Override
    public void deactivate(String username, String password) {
        checkCredentials(username, password);
        LOG.info("Deactivating user: {}", username);
        User user = findByUsernameInternal(username);
        if (!user.isActive()) {
            throw new ValidationException("User is already inactive: " + username);
        }
        user.setActive(false);
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
    public boolean matchPasswordOnly(String username, String password) {
        LOG.debug("Matching password: {}", username);
        return userDao.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
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

        if (!isUsernameExists(baseUsername)) {
            username = baseUsername;
        } else {
            int serialNumber = 1;

            while (isUsernameExists(baseUsername + serialNumber)) {
                serialNumber++;
            }

            username = baseUsername + serialNumber;
        }

        LOG.debug("Generated username: {}", username);
        return username;
    }

    public void checkCredentials(String username, String password) {
        if (!matchCredentials(username, password)) {
            throw new AuthenticationException("Username or password is incorrect");
        }
    }

    private void validateRequiredFields(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }

    private boolean isUsernameExists(String username) {
        return userDao.existsByUsername(username);
    }
}
