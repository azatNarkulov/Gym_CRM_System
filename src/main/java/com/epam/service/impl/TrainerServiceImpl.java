package com.epam.service.impl;

import com.epam.domain.Trainer;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainerDao;
import com.epam.service.TrainerService;
import com.epam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TrainerDao trainerDao;

    @Override
    public Trainer add(Trainer trainer) {
        validateRequiredSpecialization(trainer);
        return (Trainer) userService.add(trainer);
    }

    @Override
    public Trainer update(String username, String password, Trainer trainer) {
        userService.checkCredentials(username, password);
        validateRequiredSpecialization(trainer);
        return (Trainer) userService.updateInternal(trainer);
    }

    @Override
    public Trainer findByUsername(String username, String password) {
        return (Trainer) userService.findByUsername(username, password);
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
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername, String password) {
        userService.checkCredentials(traineeUsername, password);
        LOG.debug("Get trainers not assigned to trainee {}", traineeUsername);
        return trainerDao.findNotAssignedByTrainee(traineeUsername);
    }

    private void validateRequiredSpecialization(Trainer trainer) {
        if (trainer.getSpecialization() == null) {
            throw new ValidationException("Specialization is required");
        }
    }
}
