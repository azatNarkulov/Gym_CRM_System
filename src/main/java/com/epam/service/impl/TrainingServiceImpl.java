package com.epam.service.impl;

import com.epam.actuator.GymMetrics;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;
import com.epam.dto.request.AddTrainingRequest;
import com.epam.repository.TrainingDao;
import com.epam.service.TrainingService;
import com.epam.service.TrainingTypeService;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final UserService userService;
    private final TrainingDao trainingDao;
    private final TrainingTypeService trainingTypeService;
    private final GymMetrics gymMetrics;

    public TrainingServiceImpl(UserService userService, TrainingDao trainingDao, TrainingTypeService trainingTypeService, GymMetrics gymMetrics) {
        this.userService = userService;
        this.trainingDao = trainingDao;
        this.trainingTypeService = trainingTypeService;
        this.gymMetrics = gymMetrics;
    }

    @Override
    public Training add(AddTrainingRequest request) {
        LOG.info("Creating training: name={}", request.getTrainingName());
        Trainee trainee = (Trainee) userService.findByUsername(request.getTraineeUsername());
        Trainer trainer = (Trainer) userService.findByUsername(request.getTrainerUsername());

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setTrainingDuration(request.getTrainingDuration());
        training.setTrainingType(trainingTypeService.findByName(TrainingTypeName.valueOf(request.getTrainingType())));

        Training saved = trainingDao.add(training);
        gymMetrics.incrementTrainingRegistrations();
        return saved;
    }

    @Override
    public List<Training> getTraineeTrainings(String username,  TrainingFilterDTO filter) {
        LOG.debug("Get trainings for trainee: {}", username);
        return trainingDao.findByTraineeUsernameAndCriteria(filter);
    }

    @Override
    public List<Training> getTrainerTrainings(String username, TrainingFilterDTO filter) {
        LOG.debug("Get trainings for trainer: {}", username);
        return trainingDao.findByTrainerUsernameAndCriteria(filter);
    }
}
