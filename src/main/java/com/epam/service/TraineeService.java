package com.epam.service;

import com.epam.util.UsernameGenerator;
import com.epam.object.Trainee;
import com.epam.repository.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;

    private UsernameGenerator usernameGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    public Trainee addTrainee(Trainee trainee) {
        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        trainee.setUsername(usernameGenerator.generate(trainee));
        trainee.setGeneratedPassword();

        return traineeDao.addTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating trainee: id={}", trainee.getUserId());

        return traineeDao.updateTrainee(trainee);
    }

    public void deleteTrainee(Long userId) {
        log.info("Deleting trainee: id={}", userId);

        traineeDao.deleteTrainee(userId);
    }

    public Trainee getTrainee(Long userId) {
        log.debug("Getting trainee: id={}", userId);

        return traineeDao.getTrainee(userId);
    }
}
