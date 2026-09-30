package com.epam.repository;

import com.epam.domain.Trainer;
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

        Trainer result = trainerDao.add(trainer);

        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertSame(trainer, result);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = generateTrainer();
        trainerDao.add(trainer);

        trainer.setFirstName("Frodo");

        Trainer result = trainerDao.update(trainer);

        assertEquals("Frodo", result.getFirstName());
        assertSame(trainer, trainerDao.get(1L));
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = generateTrainer();
        trainerDao.add(trainer);

        Trainer result = trainerDao.get(1L);

        assertSame(trainer, result);
    }

    @Test
    public void shouldFindExistingUsername() {
        Trainer trainer = generateTrainer();
        trainerDao.add(trainer);

        assertTrue(trainerDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnFalseForNonExistingUsername() {
        assertFalse(trainerDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldGenerateNextId() {
        Trainer firstTrainer = generateTrainer();
        trainerDao.add(firstTrainer);

        Trainer secondTrainer = generateTrainer();
        trainerDao.add(secondTrainer);

        assertEquals(1L, firstTrainer.getId());
        assertEquals(2L, secondTrainer.getId());
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("Baggins");
        trainer.setUsername("Bilbo.Baggins");
        return trainer;
    }
}
