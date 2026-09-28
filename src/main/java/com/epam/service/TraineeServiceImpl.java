package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.repository.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
        setUserDao(traineeDao);
    }

    @Override
    public boolean delete(Long id) {
        LOG.info("Deleting trainee: id={}", id);

        return traineeDao.delete(id);
    }
}
