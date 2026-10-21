package com.epam.service.impl;

import com.epam.actuator.GymMetrics;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.repository.TraineeDao;
import com.epam.service.TraineeService;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final UserService userService;
    private final TraineeDao traineeDao;
    private final GymMetrics gymMetrics;

    public TraineeServiceImpl(UserService userService, TraineeDao traineeDao, GymMetrics gymMetrics) {
        this.userService = userService;
        this.traineeDao = traineeDao;
        this.gymMetrics = gymMetrics;
    }

    @Override
    public Trainee add(Trainee trainee) {
        LOG.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());
        userService.prepareForCreation(trainee);
        Trainee saved = traineeDao.add(trainee);
        gymMetrics.incrementTraineeRegistrations();
        return saved;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return (Trainee) userService.update(trainee);
    }

    @Override
    public Trainee findByUsername(String username) {
        return (Trainee) userService.findByUsername(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public boolean deleteByUsername(String username) {
        LOG.info("Deleting trainee: {}", username);
        return traineeDao.delete(username);
    }

    @Override
    public void updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        LOG.info("Updating trainers for trainee: {}", traineeUsername);
        Trainee trainee = (Trainee) userService.findByUsername(traineeUsername);

        Set<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = (Trainer) userService.findByUsername(trainerUsername);
            trainers.add(trainer);
        }

        trainee.setTrainers(trainers);
    }
}
