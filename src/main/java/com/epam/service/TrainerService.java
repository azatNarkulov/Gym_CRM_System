package com.epam.service;

import com.epam.util.UsernameGenerator;
import com.epam.object.Trainer;
import com.epam.repository.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;

    private UsernameGenerator usernameGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    public Trainer addTrainer(Trainer trainer) {
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        trainer.setUsername(usernameGenerator.generate(trainer));
        trainer.setGeneratedPassword();

        return trainerDao.addTrainer(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.info("Updating trainer: id={}", trainer.getUserId());

        return trainerDao.updateTrainer(trainer);
    }

    public Trainer getTrainer(Long userId) {
        log.debug("Getting trainer: id={}", userId);

        return trainerDao.getTrainer(userId);
    }
}
