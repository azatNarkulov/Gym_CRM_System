package com.epam.util;

import com.epam.domain.User;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(UsernameGenerator.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public String generate(User user) {

        String baseUsername = user.getFirstName() + "." + user.getLastName();
        String username;

        if (!isUsernameExists(baseUsername)) {
            username = baseUsername;
        } else {
            int serialNumber = 1;

            while (isUsernameExists(baseUsername + serialNumber)) {
                serialNumber++;
            }

            username = baseUsername + serialNumber;
        }

        LOG.debug("Generated username: {}", username);
        return username;
    }

    private boolean isUsernameExists(String username) {
        return traineeDao.existsByUsername(username) || trainerDao.existsByUsername(username);
    }
}
