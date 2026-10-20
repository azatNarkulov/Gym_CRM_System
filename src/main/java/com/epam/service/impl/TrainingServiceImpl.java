package com.epam.service.impl;

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

    @Autowired
    private TrainingTypeService trainingTypeService;

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

        return trainingDao.add(training);
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
