package com.epam.service;

import com.epam.object.Training;
import com.epam.repository.TrainingDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainingServiceTest {

    private TrainingService trainingService;
    private TrainingDao trainingDao;

    @BeforeEach
    public void setUp() {
        trainingService = new TrainingService();

        trainingDao = mock(TrainingDao.class);

        trainingService.setTrainingDao(trainingDao);
    }

    @Test
    public void shouldAddTraining() {
        Training training = generateTraining();

        when(trainingDao.addTraining(training)).thenReturn(training);

        Training result = trainingService.addTraining(training);

        assertSame(training, result);

        verify(trainingDao).addTraining(training);
    }

    @Test
    public void shouldGetTraining() {
        Training training = generateTraining();

        when(trainingDao.getTraining(1L)).thenReturn(training);

        Training result = trainingService.getTraining(1L);

        assertSame(training, result);

        verify(trainingDao).getTraining(1L);
    }

    private Training generateTraining() {
        Training training = new Training();
        training.setTrainingName("Saturday Stretching");
        return training;
    }
}
