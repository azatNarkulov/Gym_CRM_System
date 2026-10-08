package com.epam.repository;

import com.epam.domain.Trainer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerDao extends AbstractUserDao<Trainer> {

    private static final String QUERY_FIND_NOT_ASSIGNED_BY_TRAINEE =
            "SELECT trainer FROM Trainer trainer WHERE trainer NOT IN (" +
            "    SELECT trainer2 FROM Trainee trainee JOIN trainee.trainers trainer2 WHERE trainee.username = :username)";

    @Override
    protected Class<Trainer> getEntityClass() {
        return Trainer.class;
    }

    public List<Trainer> findNotAssignedByTrainee(String traineeUsername) {
        return entityManager.createQuery(QUERY_FIND_NOT_ASSIGNED_BY_TRAINEE, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }
}
