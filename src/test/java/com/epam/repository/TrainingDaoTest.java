package com.epam.repository;

import com.epam.object.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TrainingDaoTest {

    private TrainingDao trainingDao;

    @BeforeEach
    public void setUp() {
        trainingDao = new TrainingDao();
        trainingDao.setTrainingMap(new HashMap<>());
    }

    @Test
    public void shouldAddTraining() {
        Training training = generateTraining();

        Training result = trainingDao.addTraining(training);

        assertNotNull(result.getTrainingId());
        assertEquals(1L, result.getTrainingId());
        assertSame(training, result);
    }

    @Test
    public void shouldGetTraining() {
        Training training = generateTraining();
        trainingDao.addTraining(training);

        Training result = trainingDao.getTraining(1L);

        assertSame(training, result);
    }

    @Test
    public void shouldGenerateNextId() {
        Training firsttraining = generateTraining();
        trainingDao.addTraining(firsttraining);

        Training secondtraining = generateTraining();
        trainingDao.addTraining(secondtraining);

        assertEquals(1L, firsttraining.getTrainingId());
        assertEquals(2L, secondtraining.getTrainingId());
    }

    private Training generateTraining() {
        Training training = new Training();
        training.setTrainingName("Saturday Stretching");
        return training;
    }
}
