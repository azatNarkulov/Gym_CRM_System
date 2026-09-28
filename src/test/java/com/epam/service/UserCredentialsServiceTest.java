package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserCredentialsServiceTest {

    private UserCredentialsService userCredentialsService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @BeforeEach
    public void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        UserCredentialsServiceImpl userCredentialsServiceImpl = new UserCredentialsServiceImpl();
        userCredentialsServiceImpl.setTraineeDao(traineeDao);
        userCredentialsServiceImpl.setTrainerDao(trainerDao);
        userCredentialsService = userCredentialsServiceImpl;
    }

    @Test
    public void shouldGeneratePasswordWithCorrectLength() {
        String password = userCredentialsService.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    public void shouldGenerateDifferentPasswords() {
        String firstPassword = userCredentialsService.generatePassword();
        String secondPassword = userCredentialsService.generatePassword();

        assertNotEquals(firstPassword, secondPassword);
    }

    @Test
    public void shouldGenerateBaseUsernameWhenUsernameDoesNotExist() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(trainerDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);

        String result = userCredentialsService.generateUsername(trainee);

        assertEquals("Bilbo.Baggins", result);

        verify(traineeDao).existsByUsername("Bilbo.Baggins");
        verify(trainerDao).existsByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldGenerateUsernameWithSuffixWhenBaseUsernameExists() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);
        when(traineeDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        String result = userCredentialsService.generateUsername(trainee);

        assertEquals("Bilbo.Baggins1", result);
    }

    @Test
    public void shouldCheckAllRepositories() {
        Trainee trainee = generateUser();

        when(traineeDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(trainerDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);
        when(traineeDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        String result = userCredentialsService.generateUsername(trainee);

        assertEquals("Bilbo.Baggins1", result);
    }

    private Trainee generateUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
