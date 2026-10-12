package com.epam.service.impl;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.repository.TraineeDao;
import com.epam.service.TraineeService;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Trainee add(Trainee trainee) {
        return (Trainee) userService.add(trainee);
    }

    @Override
    public Trainee update(String username, String password, Trainee trainee) {
        return (Trainee) userService.update(username, password, trainee);
    }

    @Override
    public Trainee findByUsername(String username, String password) {
        return (Trainee) userService.findByUsername(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public void activate(String username, String password) {
        userService.activate(username, password);
    }

    @Override
    public void deactivate(String username, String password) {
        userService.deactivate(username, password);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        return userService.matchCredentials(username, password);
    }

    @Override
    public boolean deleteByUsername(String username, String password) {
        userService.checkCredentials(username, password);
        LOG.info("Deleting trainee: {}", username);
        return traineeDao.delete(username);
    }

    @Override
    public void updateTrainers(String traineeUsername, String password, List<String> trainerUsernames) {
        userService.checkCredentials(traineeUsername, password);
        LOG.info("Updating trainers for trainee: {}", traineeUsername);
        Trainee trainee = (Trainee) userService.findByUsernameInternal(traineeUsername);

        Set<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = (Trainer) userService.findByUsernameInternal(trainerUsername);
            trainers.add(trainer);
        }

        trainee.setTrainers(trainers);
    }
}
