package com.epam.repository;

import com.epam.domain.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDao extends AbstractUserDao<Trainee> {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeDao.class);

    @Override
    protected Class<Trainee> getEntityClass() {
        return Trainee.class;
    }

    public boolean delete(Long id) {
        Trainee trainee = get(id);
        if (trainee == null) {
            return false;
        }
        entityManager.remove(trainee);

        LOG.debug("Trainee deleted: id={}", id);
        return true;
    }
}
