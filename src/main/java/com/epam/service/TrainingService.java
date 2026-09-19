package com.epam.service;

import com.epam.object.Training;
import com.epam.repository.TrainingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training addTraining(Training training) {
        log.info("Creating training: name={}", training.getTrainingName());

        return trainingDao.addTraining(training);
    }

    public Training getTraining(Long trainingId) {
        log.debug("Getting training: id={}", trainingId);

        return trainingDao.getTraining(trainingId);
    }
}
