package com.epam.repository;

import com.epam.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserDao<T extends User> extends AbstractDao<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUserDao.class);

    public T update(T user) {
        getStorage().put(user.getId(), user);

        LOG.debug("User updated: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    public boolean existsByUsername(String username) {
        LOG.debug("Checking username: {}", username);

        return getStorage().values().stream()
                .anyMatch(user -> username.equals(user.getUsername()));
    }

}
