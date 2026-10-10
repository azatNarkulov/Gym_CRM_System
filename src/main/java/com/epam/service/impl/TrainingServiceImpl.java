package com.epam.service.impl;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainingDao;
import com.epam.service.TrainingService;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training add(String username, String password, Training training) {
        userService.checkCredentials(username, password);
        validateTraining(training);
        LOG.info("Creating training: name={}", training.getTrainingName());
        return trainingDao.add(training);
    }

    @Override
    public List<Training> getTraineeTrainings(String username, String password,  TrainingFilterDTO filter) {
        userService.checkCredentials(username, password);
        LOG.debug("Get trainings for trainee: {}", username);
        return trainingDao.findByTraineeUsernameAndCriteria(filter);
    }

    @Override
    public List<Training> getTrainerTrainings(String username, String password, TrainingFilterDTO filter) {
        userService.checkCredentials(username, password);
        LOG.debug("Get trainings for trainer: {}", username);
        return trainingDao.findByTrainerUsernameAndCriteria(filter);
    }

    private void validateTraining(Training training) {
        if (training.getTrainee() == null) {
            throw new ValidationException("Trainee is required");
        }
        if (training.getTrainer() == null) {
            throw new ValidationException("Trainer is required");
        }
        if (training.getTrainingType() == null) {
            throw new ValidationException("Training type is required");
        }
        if (training.getTrainingName() == null || training.getTrainingName().isBlank()) {
            throw new ValidationException("Training name is required");
        }
        if (training.getTrainingDate() == null) {
            throw new ValidationException("Training date is required");
        }
        if (training.getTrainingDuration() == null) {
            throw new ValidationException("Training duration is required");
        }
    }
}
