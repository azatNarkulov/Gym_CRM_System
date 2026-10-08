package com.epam.service.impl;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.exception.UserNotFoundException;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import com.epam.repository.TrainingDao;
import com.epam.service.AbstractUserService;
import com.epam.service.TraineeService;
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
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public boolean delete(Long id) {
        LOG.info("Deleting trainee: id={}", id);

        return traineeDao.delete(id);
    }

    @Override
    public boolean deleteByUsername(String username) {
        Trainee trainee = findByUsername(username);
        trainingDao.deleteByTraineeId(trainee.getId());
        return traineeDao.delete(trainee.getId());
    }

    @Override
    public void updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = findByUsername(traineeUsername);

        Set<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerDao.findByUsername(trainerUsername)
                    .orElseThrow(() -> new UserNotFoundException(trainerUsername));
            trainers.add(trainer);
        }

        trainee.setTrainers(trainers);
        LOG.info("Updating trainers for trainee: {}", traineeUsername);
    }
}
