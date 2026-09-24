package com.epam.service;

import com.epam.domain.Training;
import com.epam.repository.TrainingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training add(Training training) {
        LOG.info("Creating training: name={}", training.getTrainingName());

        return trainingDao.add(training);
    }

    public Optional<Training> get(Long id) {
        LOG.debug("Getting training: id={}", id);

        return Optional.ofNullable(trainingDao.get(id));
    }
}
