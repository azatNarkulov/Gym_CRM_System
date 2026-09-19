package com.epam.repository;

import com.epam.object.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingDao {

    private static final Logger log = LoggerFactory.getLogger(TrainingDao.class);

    private Map<Long, Training> trainingMap;

    @Autowired
    public void setTrainingMap(Map<Long, Training> trainingMap) {
        this.trainingMap = trainingMap;
    }

    public Training addTraining(Training training) {
        Long id = generateId();
        training.setTrainingId(id);
        trainingMap.put(id, training);

        log.info("Training added: id={}, name={}", training.getTrainingId(), training.getTrainingName());
        return training;
    }

    public Training getTraining(Long trainingId) {
        log.debug("Searching for training: id={}", trainingId);

        return trainingMap.get(trainingId);
    }

    private Long generateId() {
        return trainingMap.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
