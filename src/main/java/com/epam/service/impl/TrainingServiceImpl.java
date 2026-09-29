package com.epam.service.impl;

import com.epam.domain.Training;
import com.epam.repository.TrainingDao;
import com.epam.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training add(Training training) {
        LOG.info("Creating training: name={}", training.getTrainingName());

        return trainingDao.add(training);
    }

    @Override
    public Optional<Training> get(Long id) {
        LOG.debug("Getting training: id={}", id);

        return Optional.ofNullable(trainingDao.get(id));
    }
}
