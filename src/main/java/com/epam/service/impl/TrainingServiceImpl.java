package com.epam.service.impl;

import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainingDao;
import com.epam.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training add(Training training) {
        validateTraining(training);
        LOG.info("Creating training: name={}", training.getTrainingName());
        return trainingDao.add(training);
    }

    @Override
    public Optional<Training> get(Long id) {
        LOG.debug("Getting training: id={}", id);

        return Optional.ofNullable(trainingDao.get(id));
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

    @Override
    public List<Training> getTraineeTrainings(String username, LocalDate from, LocalDate to, String trainerName, TrainingTypeName type) {
        return trainingDao.findByTraineeUsernameAndCriteria(username, from, to, trainerName, type);
    }

    @Override
    public List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to, String traineeName) {
        return trainingDao.findByTrainerUsernameAndCriteria(username, from, to, traineeName);
    }
}
