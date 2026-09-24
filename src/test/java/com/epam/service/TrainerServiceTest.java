package com.epam.service;

import com.epam.domain.Trainer;
import com.epam.repository.TrainerDao;
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

public class TrainerServiceTest {

    private TrainerService trainerService;
    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @BeforeEach
    public void setUp() {
        trainerDao = mock(TrainerDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);

        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUsernameGenerator(usernameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = generateTrainer();

        when(usernameGenerator.generate(trainer)).thenReturn("Bilbo.Baggins");
        when(passwordGenerator.generate()).thenReturn("password12");
        when(trainerDao.add(trainer)).thenReturn(trainer);

        Trainer result = trainerService.add(trainer);

        assertSame(trainer, result);
        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals("password12", result.getPassword());

        verify(usernameGenerator).generate(trainer);
        verify(passwordGenerator).generate();
        verify(trainerDao).add(trainer);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = generateTrainer();

        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update(trainer);

        assertSame(trainer, result);

        verify(trainerDao).update(trainer);
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
