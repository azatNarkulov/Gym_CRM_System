package com.epam.util;

import com.epam.object.User;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameGenerator {

    private static final Logger log = LoggerFactory.getLogger(UsernameGenerator.class);

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

        String baseUserName = user.getFirstName() + "." + user.getLastName();

        if (!isUsernameExists(baseUserName)) {
            log.debug("Generated username: {}", baseUserName);
            return baseUserName;
        }

        int serialNumber = 1;

        while (isUsernameExists(baseUserName + serialNumber)) {
            serialNumber++;
        }

        String username = baseUserName + serialNumber;

        log.debug("Generated username with suffix: {}", username);

        return username;
    }

    private boolean isUsernameExists(String username) {
        return traineeDao.existsByUsername(username) || trainerDao.existsByUsername(username);
    }
}
