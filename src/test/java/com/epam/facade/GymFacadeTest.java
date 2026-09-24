package com.epam.facade;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GymFacadeTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    public void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);

        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = new Trainee();

        when(traineeService.add(trainee)).thenReturn(trainee);

        assertSame(trainee, gymFacade.addTrainee(trainee));

        verify(traineeService).add(trainee);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = new Trainee();

        when(traineeService.update(trainee)).thenReturn(trainee);

        assertSame(trainee, gymFacade.updateTrainee(trainee));

        verify(traineeService).update(trainee);
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = new Trainee();

        when(traineeService.get(1L)).thenReturn(Optional.of(trainee));

        assertEquals(Optional.of(trainee), gymFacade.getTrainee(1L));

        verify(traineeService).get(1L);
    }

    @Test
    public void shouldDeleteTrainee() {
        when(traineeService.delete(1L)).thenReturn(true);

        assertTrue(gymFacade.deleteTrainee(1L));

        verify(traineeService).delete(1L);
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = new Trainer();

        when(trainerService.add(trainer)).thenReturn(trainer);

        assertSame(trainer, gymFacade.addTrainer(trainer));

        verify(trainerService).add(trainer);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = new Trainer();

        when(trainerService.update(trainer)).thenReturn(trainer);

        assertSame(trainer, gymFacade.updateTrainer(trainer));

        verify(trainerService).update(trainer);
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = new Trainer();

        when(trainerService.get(1L)).thenReturn(Optional.of(trainer));

        assertEquals(Optional.of(trainer), gymFacade.getTrainer(1L));

        verify(trainerService).get(1L);
    }

    @Test
    public void shouldAddTraining() {
        Training training = new Training();

        when(trainingService.add(training)).thenReturn(training);

        assertSame(training, gymFacade.addTraining(training));

        verify(trainingService).add(training);
    }

    @Test
    public void shouldGetTraining() {
        Training training = new Training();

        when(trainingService.get(1L)).thenReturn(Optional.of(training));

        assertEquals(Optional.of(training), gymFacade.getTraining(1L));

        verify(trainingService).get(1L);
    }
}
