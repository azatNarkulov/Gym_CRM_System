package com.epam.repository;

import com.epam.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerDao extends AbstractDao<Trainer> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerDao.class);

    private static final String QUERY_FIND_NOT_ASSIGNED_BY_TRAINEE =
            "SELECT trainer FROM Trainer trainer WHERE trainer NOT IN (" +
            "    SELECT trainer2 FROM Trainee trainee JOIN trainee.trainers trainer2 WHERE trainee.username = :username)";
    private static final String QUERY_EXISTS_BY_USERNAME =
            "SELECT t.id FROM Trainer t WHERE t.firstName = :firstName AND t.lastName = :lastName";

    public List<Trainer> findNotAssignedByTrainee(String traineeUsername) {
        LOG.debug("Finding trainers by traineeUsername={}", traineeUsername);
        return getEntityManager().createQuery(QUERY_FIND_NOT_ASSIGNED_BY_TRAINEE, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    public boolean existsByFirstNameAndLastName(String firstName, String lastName) {
        return !getEntityManager()
                .createQuery(QUERY_EXISTS_BY_USERNAME, Long.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }

    @Override
    protected Class<Trainer> getEntityClass() {
        return Trainer.class;
    }
}
