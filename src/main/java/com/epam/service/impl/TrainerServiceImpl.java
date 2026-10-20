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
        LOG.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());
        userService.prepareForCreation(trainer);
        return trainerDao.add(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        validateRequiredSpecialization(trainer);
        return (Trainer) userService.update(trainer);
    }

    @Override
    public Trainer findByUsername(String username) {
        return (Trainer) userService.findByUsername(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        LOG.debug("Get trainers not assigned to trainee {}", traineeUsername);
        return trainerDao.findNotAssignedByTrainee(traineeUsername);
    }

    private void validateRequiredSpecialization(Trainer trainer) {
        if (trainer.getSpecialization() == null) {
            throw new ValidationException("Specialization is required");
        }
    }
}
