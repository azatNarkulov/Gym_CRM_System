package com.epam.repository;

import com.epam.object.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerDaoTest {

    private TrainerDao trainerDao;

    @BeforeEach
    public void setUp() {
        trainerDao = new TrainerDao();
        trainerDao.setTrainerMap(new HashMap<>());
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = generateTrainer();

        Trainer result = trainerDao.addTrainer(trainer);

        assertNotNull(result.getUserId());
        assertEquals(1L, result.getUserId());
        assertSame(trainer, result);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = generateTrainer();

        trainerDao.addTrainer(trainer);

        trainer.setFirstName("Frodo");

        Trainer result = trainerDao.updateTrainer(trainer);

        assertEquals("Frodo", result.getFirstName());
        assertSame(trainer, trainerDao.getTrainer(1L));
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = generateTrainer();
        trainerDao.addTrainer(trainer);

        Trainer result = trainerDao.getTrainer(1L);

        assertSame(trainer, result);
    }

    @Test
    public void shouldFindExistingUsername() {
        Trainer trainer = generateTrainer();
        trainerDao.addTrainer(trainer);

        assertTrue(trainerDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnFalseForNonExistingUsername() {
        assertFalse(trainerDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldGenerateNextId() {
        Trainer firsttrainer = generateTrainer();
        trainerDao.addTrainer(firsttrainer);

        Trainer secondtrainer = generateTrainer();
        trainerDao.addTrainer(secondtrainer);

        assertEquals(1L, firsttrainer.getUserId());
        assertEquals(2L, secondtrainer.getUserId());
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("Baggins");
        trainer.setUsername("Bilbo.Baggins");
        return trainer;
    }
}
