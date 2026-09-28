package com.epam.service;

import com.epam.domain.Trainer;
import com.epam.exception.UserNotFoundException;
import com.epam.repository.TrainerDao;
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

public class TrainerServiceTest {

    private TrainerService trainerService;
    private TrainerDao trainerDao;
    private UserCredentialsService userCredentialsService;

    @BeforeEach
    public void setUp() {
        trainerDao = mock(TrainerDao.class);
        userCredentialsService = mock(UserCredentialsService.class);

        TrainerServiceImpl trainerServiceImpl = new TrainerServiceImpl();
        trainerServiceImpl.setTrainerDao(trainerDao);
        trainerServiceImpl.setUserCredentialsService(userCredentialsService);

        trainerService = trainerServiceImpl;
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = generateTrainer();

        when(userCredentialsService.generateUsername(trainer)).thenReturn("Bilbo.Baggins");
        when(userCredentialsService.generatePassword()).thenReturn("password12");
        when(trainerDao.add(trainer)).thenReturn(trainer);

        Trainer result = trainerService.add(trainer);

        assertSame(trainer, result);
        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals("password12", result.getPassword());

        verify(userCredentialsService).generateUsername(trainer);
        verify(userCredentialsService).generatePassword();
        verify(trainerDao).add(trainer);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = generateTrainer();
        trainer.setId(1L);

        when(trainerDao.get(1L)).thenReturn(trainer);
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update(trainer);

        assertSame(trainer, result);

        verify(trainerDao).get(1L);
        verify(trainerDao).update(trainer);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateTraineeNotFound() {
        Trainer trainer = generateTrainer();
        trainer.setId(1L);

        when(trainerDao.get(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> trainerService.update(trainer));

        verify(trainerDao).get(1L);
        verify(trainerDao, never()).update(trainer);
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = generateTrainer();

        when(trainerDao.get(1L)).thenReturn(trainer);

        Optional<Trainer> result = trainerService.get(1L);

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());

        verify(trainerDao).get(1L);
    }

    @Test
    public void shouldReturnEmptyWhenTrainerDoesNotExist() {
        when(trainerDao.get(1L)).thenReturn(null);

        Optional<Trainer> result = trainerService.get(1L);

        assertFalse(result.isPresent());

        verify(trainerDao).get(1L);
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("Baggins");
        return trainer;
    }
}
