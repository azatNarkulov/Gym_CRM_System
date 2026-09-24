package com.epam.repository;

import com.epam.domain.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeDao extends AbstractUserDao<Trainee> {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeDao.class);

    @Autowired
    public void setTraineeMap(@Qualifier("traineeMap") Map<Long, Trainee> traineeMap) {
        setStorage(traineeMap);
    }

    public boolean delete(Long id) {
        boolean removed = storage.remove(id) != null;

        LOG.info("Trainee deleted: id={}, removed={}", id, removed);
        return removed;
    }
}
