package com.epam.service;

import com.epam.domain.Training;
import com.epam.repository.TrainingDao;
import com.epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingDao trainingDao;

    @Test
    public void shouldAddTraining() {
        Training training = generateTraining();

        when(trainingDao.add(training)).thenReturn(training);

        Training result = trainingService.add(training);

        assertSame(training, result);

        verify(trainingDao).add(training);
    }

    @Test
    public void shouldGetTraining() {
        Training training = generateTraining();

        when(trainingDao.get(1L)).thenReturn(training);

        Optional<Training> result = trainingService.get(1L);

        assertTrue(result.isPresent());
        assertSame(training, result.get());

        verify(trainingDao).get(1L);
    }

    @Test
    public void shouldReturnEmptyWhenTrainingDoesNotExist() {
        when(trainingDao.get(1L)).thenReturn(null);

        Optional<Training> result = trainingService.get(1L);

        assertFalse(result.isPresent());

        verify(trainingDao).get(1L);
    }

    private Training generateTraining() {
        Training training = new Training();
        training.setTrainingName("Saturday Stretching");
        return training;
    }
}
