package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.exception.UserNotFoundException;
import com.epam.repository.TraineeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeDao traineeDao;
    private UserCredentialsService userCredentialsService;

    @BeforeEach
    public void setUp() {
        traineeDao = mock(TraineeDao.class);
        userCredentialsService = mock(UserCredentialsService.class);

        TraineeServiceImpl traineeServiceImpl = new TraineeServiceImpl();
        traineeServiceImpl.setTraineeDao(traineeDao);
        traineeServiceImpl.setUserCredentialsService(userCredentialsService);

        traineeService = traineeServiceImpl;
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = generateTrainee();

        when(userCredentialsService.generateUsername(trainee)).thenReturn("Bilbo.Baggins");
        when(userCredentialsService.generatePassword()).thenReturn("password12");
        when(traineeDao.add(trainee)).thenReturn(trainee);

        Trainee result = traineeService.add(trainee);

        assertSame(trainee, result);
        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals("password12", result.getPassword());

        verify(userCredentialsService).generateUsername(trainee);
        verify(userCredentialsService).generatePassword();
        verify(traineeDao).add(trainee);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = generateTrainee();
        trainee.setId(1L);

        when(traineeDao.get(1L)).thenReturn(trainee);
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.update(trainee);

        assertSame(trainee, result);

        verify(traineeDao).get(1L);
        verify(traineeDao).update(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateTraineeNotFound() {
        Trainee trainee = generateTrainee();
        trainee.setId(1L);

        when(traineeDao.get(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> traineeService.update(trainee));

        verify(traineeDao).get(1L);
        verify(traineeDao, never()).update(trainee);
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
