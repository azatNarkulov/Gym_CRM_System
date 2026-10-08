package com.epam.repository;

import com.epam.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractUserDao<T extends User> extends AbstractDao<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUserDao.class);

    private static final String QUERY_FIND_BY_USERNAME = "SELECT u FROM User u WHERE u.username = :username";
    private static final String QUERY_EXISTS_BY_USERNAME = "SELECT COUNT (u) FROM User u WHERE u.username = :username";

    public T update(T user) {
        LOG.debug("User updated: id={}, username={}", user.getId(), user.getUsername());
        return entityManager.merge(user);
    }

    public boolean existsByUsername(String username) {
        LOG.debug("Checking username: {}", username);
        Long count = entityManager
                .createQuery(QUERY_EXISTS_BY_USERNAME, Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }

    public Optional<T> findByUsername(String username) {
        return entityManager
                .createQuery(QUERY_FIND_BY_USERNAME, (Class<T>) getEntityClass())
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

}
