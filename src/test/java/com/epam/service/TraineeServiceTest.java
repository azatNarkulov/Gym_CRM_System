package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.repository.TraineeDao;
import com.epam.util.PasswordGenerator;
import com.epam.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @BeforeEach
    public void setUp() {
        traineeDao = mock(TraineeDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);

        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUsernameGenerator(usernameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = generateTrainee();

        when(usernameGenerator.generate(trainee)).thenReturn("Bilbo.Baggins");
        when(passwordGenerator.generate()).thenReturn("password12");
        when(traineeDao.add(trainee)).thenReturn(trainee);

        Trainee result = traineeService.add(trainee);

        assertSame(trainee, result);
        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals("password12", result.getPassword());

        verify(usernameGenerator).generate(trainee);
        verify(passwordGenerator).generate();
        verify(traineeDao).add(trainee);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = generateTrainee();

        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.update(trainee);

        assertSame(trainee, result);

        verify(traineeDao).update(trainee);
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = generateTrainee();

        when(traineeDao.get(1L)).thenReturn(trainee);

        Optional<Trainee> result = traineeService.get(1L);

        assertTrue(result.isPresent());
        assertSame(trainee, result.get());

        verify(traineeDao).get(1L);
    }

    @Test
    public void shouldReturnEmptyWhenTraineeDoesNotExist() {
        when(traineeDao.get(1L)).thenReturn(null);

        Optional<Trainee> result = traineeService.get(1L);

        assertFalse(result.isPresent());

        verify(traineeDao).get(1L);
    }

    @Test
    public void shouldDeleteTrainee() {
        when(traineeDao.delete(1L)).thenReturn(true);

        assertTrue(traineeService.delete(1L));

        verify(traineeDao).delete(1L);
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
