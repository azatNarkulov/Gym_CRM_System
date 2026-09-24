package com.epam.repository;

import com.epam.domain.Training;
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

        Training result = trainingDao.add(training);

        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertSame(training, result);
    }

    @Test
    public void shouldGetTraining() {
        Training training = generateTraining();
        trainingDao.add(training);

        Training result = trainingDao.get(1L);

        assertSame(training, result);
    }

    @Test
    public void shouldGenerateNextId() {
        Training firstTraining = generateTraining();
        trainingDao.add(firstTraining);

        Training secondTraining = generateTraining();
        trainingDao.add(secondTraining);

        assertEquals(1L, firstTraining.getId());
        assertEquals(2L, secondTraining.getId());
    }

    private Training generateTraining() {
        Training training = new Training();
        training.setTrainingName("Saturday Stretching");
        return training;
    }
}
