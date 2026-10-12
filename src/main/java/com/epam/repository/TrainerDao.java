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

    public List<Trainer> findNotAssignedByTrainee(String traineeUsername) {
        LOG.debug("Finding trainers by traineeUsername={}", traineeUsername);
        return getEntityManager().createQuery(QUERY_FIND_NOT_ASSIGNED_BY_TRAINEE, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    @Override
    protected Class<Trainer> getEntityClass() {
        return Trainer.class;
    }
}
