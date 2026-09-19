package com.epam.repository;

import com.epam.object.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeDao {

    private static final Logger log = LoggerFactory.getLogger(TraineeDao.class);

    private Map<Long, Trainee> traineeMap;

    @Autowired
    public void setTraineeMap(Map<Long, Trainee> traineeMap) {
        this.traineeMap = traineeMap;
    }

    public Trainee addTrainee(Trainee trainee) {
        Long id = generateId();
        trainee.setUserId(id);
        traineeMap.put(id, trainee);

        log.info("Trainee added: id={}, username={}", trainee.getUserId(), trainee.getUsername());
        return trainee;
    }

    public Trainee updateTrainee(Trainee trainee) {
        traineeMap.put(trainee.getUserId(), trainee);

        log.info("Trainee updated: id={}, username={}", trainee.getUserId(), trainee.getUsername());
        return trainee;
    }

    public void deleteTrainee(Long userId) {
        traineeMap.remove(userId);

        log.info("Trainee deleted: id={}", userId);
    }

    public Trainee getTrainee(Long userId) {
        log.debug("Searching for trainee: id={}", userId);

        return traineeMap.get(userId);
    }

    public boolean existsByUsername(String username) {
        log.debug("Checking trainee username: {}", username);

        return traineeMap.values().stream()
                .anyMatch(trainee -> username.equals(trainee.getUsername()));
    }

    private Long generateId() {
        return traineeMap.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
