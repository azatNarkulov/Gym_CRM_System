package com.epam.service;

import com.epam.object.Trainee;
import com.epam.repository.TraineeDao;
import com.epam.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;

    @BeforeEach
    public void setUp() {
        traineeService = new TraineeService();

        traineeDao = mock(TraineeDao.class);
        usernameGenerator = mock(UsernameGenerator.class);

        traineeService.setTraineeDao(traineeDao);
        traineeService.setUsernameGenerator(usernameGenerator);
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = generateTrainee();

        when(usernameGenerator.generate(trainee)).thenReturn("Bilbo.Baggins");
        when(traineeDao.addTrainee(trainee)).thenReturn(trainee);

        Trainee result = traineeService.addTrainee(trainee);

        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals(10, result.getPassword().length());
        assertSame(trainee, result);

        verify(usernameGenerator).generate(trainee);
        verify(traineeDao).addTrainee(trainee);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = generateTrainee();

        when(traineeDao.updateTrainee(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainee(trainee);

        assertSame(trainee, result);

        verify(traineeDao).updateTrainee(trainee);
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = generateTrainee();

        when(traineeDao.getTrainee(1L)).thenReturn(trainee);

        Trainee result = traineeService.getTrainee(1L);

        assertSame(trainee, result);

        verify(traineeDao).getTrainee(1L);
    }

    @Test
    public void shouldDeleteTrainee() {
        traineeService.deleteTrainee(1L);

        verify(traineeDao).deleteTrainee(1L);
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
