package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import com.epam.repository.TrainingDao;
import org.junit.jupiter.api.BeforeEach;
import com.epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserCredentialsService userCredentialsService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(traineeService, "userDao", traineeDao);
    }

    @Test
    public void shouldGenerateCredentialsAndPersist() {
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
    public void shouldThrowExceptionWhenFirstNameIsNull() {
        Trainee trainee = new Trainee();
        trainee.setLastName("Baggins");
        assertThrows(ValidationException.class, () -> traineeService.add(trainee));
        verifyNoInteractions(traineeDao);
    }

    @Test
    public void shouldThrowExceptionWhenLastNameIsBlank() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("");
        assertThrows(ValidationException.class, () -> traineeService.add(trainee));
        verifyNoInteractions(traineeDao, userCredentialsService);
    }

    @Test
    public void shouldPreservePasswordAndIsActive() {
        Trainee existingTrainee = generateTrainee();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");
        existingTrainee.setPassword("password12");
        existingTrainee.setActive(true);

        Trainee traineeToUpdate = generateTrainee();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Bilbo.Baggins");
        traineeToUpdate.setPassword("password99");
        traineeToUpdate.setActive(false);

        when(traineeDao.get(1L)).thenReturn(existingTrainee);
        when(traineeDao.update(any())).thenReturn(traineeToUpdate);

        traineeService.update(traineeToUpdate);

        assertEquals("password12", traineeToUpdate.getPassword());
        assertTrue(traineeToUpdate.isActive());
        verify(traineeDao).update(traineeToUpdate);
    }

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {
        Trainee trainee = generateTrainee();
        assertThrows(UserNotFoundException.class, () -> traineeService.update(trainee));
    }

    @Test
    public void shouldThrowWhenUsernameTakenByAnother() {
        Trainee existingTrainee = generateTrainee();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");

        Trainee traineeToUpdate = generateTrainee();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Frodo.Baggins");

        when(traineeDao.get(1L)).thenReturn(existingTrainee);
        when(traineeDao.existsByUsername("Frodo.Baggins")).thenReturn(true);

        assertThrows(ValidationException.class, () -> traineeService.update(traineeToUpdate));
        verify(traineeDao, never()).update(any());
    }

    @Test
    public void shouldAllowSameUsername() {
        Trainee existingTrainee = generateTrainee();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");
        existingTrainee.setActive(true);

        Trainee traineeToUpdate = generateTrainee();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Bilbo.Baggins");

        when(traineeDao.get(1L)).thenReturn(existingTrainee);
        when(traineeDao.update(traineeToUpdate)).thenReturn(traineeToUpdate);

        traineeService.update(traineeToUpdate);

        verify(traineeDao).update(traineeToUpdate);
    }

    @Test
    public void shouldGetTrainee() {
        Trainee trainee = generateTrainee();

        when(traineeDao.get(1L)).thenReturn(trainee);

        assertEquals(Optional.of(trainee), traineeService.get(1L));
    }

    @Test
    public void shouldReturnEmptyWhenTraineeDoesNotExist() {
        when(traineeDao.get(1L)).thenReturn(null);
        assertTrue(traineeService.get(1L).isEmpty());
    }

    @Test
    public void shouldDeleteTrainee() {
        when(traineeDao.delete(1L)).thenReturn(true);

        assertTrue(traineeService.delete(1L));

        verify(traineeDao).delete(1L);
    }

    @Test
    public void shouldFindByUsername() {
        Trainee trainee = generateTrainee();
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));
        assertSame(trainee, traineeService.findByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldThrowExceptionWhenTraineeNotFound() {
        when(traineeDao.findByUsername("Frodo.Baggins")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> traineeService.findByUsername("Frodo.Baggins"));
    }

    @Test
    public void shouldUpdatePassword() {
        Trainee trainee = generateTrainee();
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        traineeService.changePassword("Bilbo.Baggins", "password99");

        assertEquals("password99", trainee.getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    public void shouldActivate() {
        Trainee trainee = generateTrainee();
        trainee.setActive(false);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        traineeService.activate("Bilbo.Baggins");

        assertTrue(trainee.isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenTraineeIsActivated() {
        Trainee trainee = generateTrainee();
        trainee.setActive(true);

        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(ValidationException.class, () -> traineeService.activate("Bilbo.Baggins"));

        verify(traineeDao, never()).update(any());
    }

    @Test
    public void shouldDeactivate() {
        Trainee trainee = generateTrainee();
        trainee.setActive(true);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        traineeService.deactivate("Bilbo.Baggins");

        assertFalse(trainee.isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenTraineeIsDeactivated() {
        Trainee trainee = generateTrainee();
        trainee.setActive(false);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(ValidationException.class, () -> traineeService.deactivate("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnTrueForValidCredentials() {
        Trainee trainee = generateTrainee();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertTrue(traineeService.matchCredentials("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldReturnFalseForInvalidCredentials() {
        Trainee trainee = generateTrainee();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertFalse(traineeService.matchCredentials("Bilbo.Baggins", "password99"));
    }

    @Test
    public void shouldDeleteByUsername() {
        Trainee trainee = generateTrainee();
        trainee.setId(1L);
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));
        when(traineeDao.delete(1L)).thenReturn(true);

        assertTrue(traineeService.deleteByUsername("Bilbo.Baggins"));

        verify(trainingDao).deleteByTraineeId(1L);
        verify(traineeDao).delete(1L);
    }

    @Test
    public void shouldSetTrainers() {
        Trainee trainee = generateTrainee();
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Trainer2");

        when(trainerDao.findByUsername("Trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerDao.findByUsername("Trainer2")).thenReturn(Optional.of(trainer2));

        traineeService.updateTrainers("Bilbo.Baggins", List.of("Trainer1", "Trainer2"));

        assertEquals(2, trainee.getTrainers().size());
        assertTrue(trainee.getTrainers().contains(trainer1));
        assertTrue(trainee.getTrainers().contains(trainer2));
    }

    @Test
    public void shouldThrowWhenTrainerNotFound() {
        Trainee trainee = generateTrainee();
        when(traineeDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("Trainer1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainers("Bilbo.Baggins", List.of("Trainer1")));
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
