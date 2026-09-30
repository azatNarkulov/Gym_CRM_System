package com.epam.service;

import com.epam.domain.User;
import com.epam.exception.UserNotFoundException;
import com.epam.repository.AbstractUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
        LOG.info("Creating user: {} {}", user.getFirstName(), user.getLastName());

        user.setUsername(userCredentialsService.generateUsername(user));
        user.setPassword(userCredentialsService.generatePassword());

        return userDao.add(user);
    }

    @Override
    public T update(T user) {
        LOG.info("Updating user: id={}", user.getId());

        Long id = user.getId();
        if (id == null || !get(id).isPresent()) {
            throw new UserNotFoundException(id);
        }
        return userDao.update(user);
    }

    @Override
    public Optional<T> get(Long id) {
        return Optional.ofNullable(userDao.get(id));
    }
}
