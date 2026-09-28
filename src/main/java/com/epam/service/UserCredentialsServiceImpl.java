package com.epam.service;

import com.epam.domain.User;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserCredentialsServiceImpl.class);

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final Random random = new SecureRandom();

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

    @Override
    public String generatePassword() {

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return password.toString();
    }

    @Override
    public String generateUsername(User user) {

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
