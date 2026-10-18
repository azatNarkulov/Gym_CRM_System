package com.epam.repository;

import com.epam.domain.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDao extends AbstractDao<Trainee> {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeDao.class);

    private static final String QUERY_DELETE_TRAINEE_BY_USERNAME =
            "DELETE FROM trainees WHERE id IN (SELECT id FROM users WHERE username = :username)";
    private static final String QUERY_DELETE_USER_BY_USERNAME = "DELETE FROM users WHERE username = :username";

    public boolean delete(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        getEntityManager()
                .createNativeQuery(QUERY_DELETE_TRAINEE_BY_USERNAME)
                .setParameter("username", username)
                .executeUpdate();

        int deletedUsers = getEntityManager()
                .createNativeQuery(QUERY_DELETE_USER_BY_USERNAME)
                .setParameter("username", username)
                .executeUpdate();

        LOG.debug("Trying delete trainee: username={}", username);
        return deletedUsers > 0;
    }

    @Override
    protected Class<Trainee> getEntityClass() {
        return Trainee.class;
    }
}
