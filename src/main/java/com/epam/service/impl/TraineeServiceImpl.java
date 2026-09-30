package com.epam.service.impl;

import com.epam.domain.Trainee;
import com.epam.repository.TraineeDao;
import com.epam.service.AbstractUserService;
import com.epam.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public boolean delete(Long id) {
        LOG.info("Deleting trainee: id={}", id);

        return traineeDao.delete(id);
    }
}
