package com.epam.util;

import com.epam.domain.Trainee;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsernameGeneratorTest {

    private UsernameGenerator usernameGenerator;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @BeforeEach
    public void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        usernameGenerator = new UsernameGenerator();
        usernameGenerator.setTraineeDao(traineeDao);
        usernameGenerator.setTrainerDao(trainerDao);
    }

    @Test
    public void shouldGenerateBaseUsernameWhenUsernameDoesNotExist() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(trainerDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);

        String result = usernameGenerator.generate(trainee);

        assertEquals("Bilbo.Baggins", result);

        verify(traineeDao).existsByUsername("Bilbo.Baggins");
        verify(trainerDao).existsByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldGenerateUsernameWithSuffixWhenBaseUsernameExists() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);
        when(traineeDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        String result = usernameGenerator.generate(trainee);

        assertEquals("Bilbo.Baggins1", result);
    }

    @Test
    public void shouldCheckAllRepositories() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(trainerDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);

        when(traineeDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        String result = usernameGenerator.generate(trainee);

        assertEquals("Bilbo.Baggins1", result);
    }

    private Trainee generateUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
