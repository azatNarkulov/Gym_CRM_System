package com.epam.repository;

import com.epam.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao extends AbstractDao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);

    private static final String QUERY_FIND_BY_USERNAME = "SELECT u FROM User u WHERE u.username = :username";
    private static final String QUERY_EXISTS_BY_USERNAME = "SELECT 1 FROM User u WHERE u.username = :username";

    public <T extends User> T update(T user) {
        LOG.debug("Updating user: id={}, username={}", user.getId(), user.getUsername());
        return getEntityManager().merge(user);
    }

    public boolean existsByUsername(String username) {
        LOG.debug("Checking username: {}", username);
        return !getEntityManager()
                .createQuery(QUERY_EXISTS_BY_USERNAME, Integer.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }

    public Optional<User> findByUsername(String username) {
        LOG.debug("Finding user by username: {}", username);
        return getEntityManager()
                .createQuery(QUERY_FIND_BY_USERNAME, (Class<User>) getEntityClass())
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
}
