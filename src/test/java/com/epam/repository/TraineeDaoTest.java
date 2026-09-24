package com.epam.repository;

import com.epam.domain.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraineeDaoTest {

    private TraineeDao traineeDao;

    @BeforeEach
    public void setUp() {
        traineeDao = new TraineeDao();
        traineeDao.setTraineeMap(new HashMap<>());
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = generateTrainee();

        Trainee result = traineeDao.add(trainee);

        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertSame(trainee, result);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = generateTrainee();
        traineeDao.add(trainee);

        trainee.setFirstName("Frodo");

        Trainee result = traineeDao.update(trainee);

        assertEquals("Frodo", result.getFirstName());
        assertSame(trainee, traineeDao.get(1L));
    }

    @Test
    public void shouldDeleteTrainee() {
        Trainee trainee = generateTrainee();
        traineeDao.add(trainee);

        boolean result = traineeDao.delete(1L);

        assertTrue(result);
        assertNull(traineeDao.get(1L));
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistingTrainee() {
        assertFalse(traineeDao.delete(1L));
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = generateTrainee();
        traineeDao.add(trainee);

        Trainee result = traineeDao.get(1L);

        assertSame(trainee, result);
    }

    @Test
    public void shouldFindExistingUsername() {
        Trainee trainee = generateTrainee();
        traineeDao.add(trainee);

        assertTrue(traineeDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnFalseForNonExistingUsername() {
        assertFalse(traineeDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldGenerateNextId() {
        Trainee firstTrainee = generateTrainee();
        traineeDao.add(firstTrainee);

        Trainee secondTrainee = generateTrainee();
        traineeDao.add(secondTrainee);

        assertEquals(1L, firstTrainee.getId());
        assertEquals(2L, secondTrainee.getId());
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        trainee.setUsername("Bilbo.Baggins");
        return trainee;
    }
}
