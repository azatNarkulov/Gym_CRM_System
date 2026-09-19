package com.epam.repository;

import com.epam.object.Trainee;
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

        Trainee result = traineeDao.addTrainee(trainee);

        assertNotNull(result.getUserId());
        assertEquals(1L, result.getUserId());
        assertSame(trainee, result);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = generateTrainee();

        traineeDao.addTrainee(trainee);

        trainee.setFirstName("Frodo");

        Trainee result = traineeDao.updateTrainee(trainee);

        assertEquals("Frodo", result.getFirstName());
        assertSame(trainee, traineeDao.getTrainee(1L));
    }

    @Test
    public void shouldDeleteTrainee() {
        Trainee trainee = generateTrainee();
        traineeDao.addTrainee(trainee);

        traineeDao.deleteTrainee(1L);

        assertNull(traineeDao.getTrainee(1L));
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = generateTrainee();
        traineeDao.addTrainee(trainee);

        Trainee result = traineeDao.getTrainee(1L);

        assertSame(trainee, result);
    }

    @Test
    public void shouldFindExistingUsername() {
        Trainee trainee = generateTrainee();
        traineeDao.addTrainee(trainee);

        assertTrue(traineeDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnFalseForNonExistingUsername() {
        assertFalse(traineeDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldGenerateNextId() {
        Trainee firstTrainee = generateTrainee();
        traineeDao.addTrainee(firstTrainee);

        Trainee secondTrainee = generateTrainee();
        traineeDao.addTrainee(secondTrainee);

        assertEquals(1L, firstTrainee.getUserId());
        assertEquals(2L, secondTrainee.getUserId());
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        trainee.setUsername("Bilbo.Baggins");
        return trainee;
    }
}
