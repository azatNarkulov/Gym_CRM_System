package com.epam.service;

import com.epam.object.Trainer;
import com.epam.repository.TrainerDao;
import com.epam.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerServiceTest {

    private TrainerService trainerService;
    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;

    @BeforeEach
    public void setUp() {
        trainerService = new TrainerService();

        trainerDao = mock(TrainerDao.class);
        usernameGenerator = mock(UsernameGenerator.class);

        trainerService.setTrainerDao(trainerDao);
        trainerService.setUsernameGenerator(usernameGenerator);
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = generateTrainer();

        when(usernameGenerator.generate(trainer)).thenReturn("Bilbo.Baggins");
        when(trainerDao.addTrainer(trainer)).thenReturn(trainer);

        Trainer result = trainerService.addTrainer(trainer);

        assertEquals("Bilbo.Baggins", result.getUsername());
        assertEquals(10, result.getPassword().length());
        assertSame(trainer, result);

        verify(usernameGenerator).generate(trainer);
        verify(trainerDao).addTrainer(trainer);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = generateTrainer();

        when(trainerDao.updateTrainer(trainer)).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer(trainer);

        assertSame(trainer, result);

        verify(trainerDao).updateTrainer(trainer);
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = generateTrainer();

        when(trainerDao.getTrainer(1L)).thenReturn(trainer);

        Trainer result = trainerService.getTrainer(1L);

        assertSame(trainer, result);

        verify(trainerDao).getTrainer(1L);
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("Baggins");
        return trainer;
    }
}
