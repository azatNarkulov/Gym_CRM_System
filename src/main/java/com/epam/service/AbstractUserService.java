package com.epam.service;

import com.epam.domain.User;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.AbstractUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public abstract class AbstractUserService<T extends User> implements UserService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUserService.class);

    @Autowired
    private AbstractUserDao<T> userDao;

    private UserCredentialsService userCredentialsService;

    @Autowired
    public void setUserCredentialsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    @Override
    public T add(T user) {
        validateRequiredFields(user);
        LOG.info("Creating user: {} {}", user.getFirstName(), user.getLastName());

        user.setUsername(userCredentialsService.generateUsername(user));
        user.setPassword(userCredentialsService.generatePassword());

        return userDao.add(user);
    }

    @Override
    public T update(T user) {
        validateRequiredFields(user);
        LOG.info("Updating user: id={}", user.getId());

        Long id = user.getId();
        if (id == null) {
            throw new UserNotFoundException();
        }

        T existing = get(id).orElseThrow(UserNotFoundException::new);

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
    public Optional<T> get(Long id) {
        return Optional.ofNullable(userDao.get(id));
    }

    public T findByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public void changePassword(String username, String newPassword) {
        T user = findByUsername(username);
        user.setPassword(newPassword);
        userDao.update(user);
    }

    public void activate(String username) {
        T user = findByUsername(username);
        if (user.isActive()) {
            throw new ValidationException("User is already active: " + username);
        }
        user.setActive(true);
        userDao.update(user);
    }

    public void deactivate(String username) {
        T user = findByUsername(username);
        if (!user.isActive()) {
            throw new ValidationException("User is already inactive: " + username);
        }
        user.setActive(false);
        userDao.update(user);
    }

    public boolean matchCredentials(String username, String password) {
        return userDao.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .filter(User::isActive)
                .isPresent();
    }

    private void validateRequiredFields(T user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }
}
