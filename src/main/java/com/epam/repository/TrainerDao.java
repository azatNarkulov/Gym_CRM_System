package com.epam.repository;

import com.epam.object.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainerDao {

    private static final Logger log = LoggerFactory.getLogger(TrainerDao.class);

    private Map<Long, Trainer> trainerMap;

    @Autowired
    public void setTrainerMap(Map<Long, Trainer> trainerMap) {
        this.trainerMap = trainerMap;
    }

    public Trainer addTrainer(Trainer trainer) {
        Long id = generateId();
        trainer.setUserId(id);
        trainerMap.put(id, trainer);

        log.info("Training added: id={}, username={}", trainer.getUserId(), trainer.getUsername());
        return trainer;
    }

    public Trainer updateTrainer(Trainer trainer) {
        trainerMap.put(trainer.getUserId(), trainer);

        log.info("Training updated: id={}, username={}", trainer.getUserId(), trainer.getUsername());
        return trainer;
    }

    public Trainer getTrainer(Long userId) {
        log.debug("Searching for trainer: id={}", userId);

        return trainerMap.get(userId);
    }

    public boolean existsByUsername(String username) {
        log.debug("Checking trainee username: {}", username);

        return trainerMap.values().stream()
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }

    private Long generateId() {
        return trainerMap.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
