package com.epam.service;

import com.epam.domain.Trainer;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainerDao;
import com.epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
public class TrainerServiceTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserCredentialsService userCredentialsService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(trainerService, "userDao", trainerDao);
    }

    @Test
    public void shouldGenerateCredentialsAndPersist() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(generateSpecialization());

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
    public void shouldThrowExceptionWhenSpecializationIsNull() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(null);

        assertThrows(ValidationException.class, () -> trainerService.add(trainer));
        verifyNoInteractions(trainerDao, userCredentialsService);
    }

    @Test
    public void shouldThrowExceptionWhenFirstNameIsNull() {
        Trainer trainer = new Trainer();
        trainer.setLastName("Baggins");
        trainer.setSpecialization(generateSpecialization());

        assertThrows(ValidationException.class, () -> trainerService.add(trainer));
        verifyNoInteractions(trainerDao);
    }

    @Test
    public void shouldThrowExceptionWhenLastNameIsBlank() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("");
        trainer.setSpecialization(generateSpecialization());

        assertThrows(ValidationException.class, () -> trainerService.add(trainer));
        verifyNoInteractions(trainerDao);
    }

    @Test
    public void shouldPreservePasswordAndIsActive() {
        Trainer existingTrainer = generateTrainer();
        existingTrainer.setId(1L);
        existingTrainer.setUsername("Bilbo.Baggins");
        existingTrainer.setPassword("password12");
        existingTrainer.setActive(true);
        existingTrainer.setSpecialization(generateSpecialization());

        Trainer trainerToUpdate = generateTrainer();
        trainerToUpdate.setId(1L);
        trainerToUpdate.setUsername("Bilbo.Baggins");
        trainerToUpdate.setPassword("password99");
        trainerToUpdate.setActive(false);
        trainerToUpdate.setSpecialization(generateSpecialization());

        when(trainerDao.get(1L)).thenReturn(existingTrainer);
        when(trainerDao.update(any())).thenReturn(trainerToUpdate);

        trainerService.update(trainerToUpdate);

        assertEquals("password12", trainerToUpdate.getPassword());
        assertTrue(trainerToUpdate.isActive());
        verify(trainerDao).update(trainerToUpdate);
    }

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(generateSpecialization());
        assertThrows(UserNotFoundException.class, () -> trainerService.update(trainer));
    }

    @Test
    public void shouldThrowWhenUsernameTakenByAnother() {
        Trainer existingTrainer = generateTrainer();
        existingTrainer.setId(1L);
        existingTrainer.setUsername("Bilbo.Baggins");

        Trainer trainerToUpdate = generateTrainer();
        trainerToUpdate.setId(1L);
        trainerToUpdate.setUsername("Frodo.Baggins");
        trainerToUpdate.setSpecialization(generateSpecialization());

        when(trainerDao.get(1L)).thenReturn(existingTrainer);
        when(trainerDao.existsByUsername("Frodo.Baggins")).thenReturn(true);

        assertThrows(ValidationException.class, () -> trainerService.update(trainerToUpdate));
        verify(trainerDao, never()).update(any());
    }

    @Test
    public void shouldAllowSameUsername() {
        Trainer existingTrainer = generateTrainer();
        existingTrainer.setId(1L);
        existingTrainer.setUsername("Bilbo.Baggins");
        existingTrainer.setActive(true);

        Trainer trainerToUpdate = generateTrainer();
        trainerToUpdate.setId(1L);
        trainerToUpdate.setUsername("Bilbo.Baggins");
        trainerToUpdate.setSpecialization(generateSpecialization());

        when(trainerDao.get(1L)).thenReturn(existingTrainer);
        when(trainerDao.update(trainerToUpdate)).thenReturn(trainerToUpdate);

        trainerService.update(trainerToUpdate);

        verify(trainerDao).update(trainerToUpdate);
    }

    @Test
    public void shouldGetTrainer() {
        Trainer trainer = generateTrainer();

        when(trainerDao.get(1L)).thenReturn(trainer);

        assertEquals(Optional.of(trainer), trainerService.get(1L));
    }

    @Test
    public void shouldReturnEmptyWhenTrainerDoesNotExist() {
        when(trainerDao.get(1L)).thenReturn(null);
        assertTrue(trainerService.get(1L).isEmpty());
    }

    @Test
    public void shouldFindByUsername() {
        Trainer trainer = generateTrainer();
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));
        assertSame(trainer, trainerService.findByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldThrowExceptionWhenTrainerNotFound() {
        when(trainerDao.findByUsername("Frodo.Baggins")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainerService.findByUsername("Frodo.Baggins"));
    }

    @Test
    public void shouldUpdatePassword() {
        Trainer trainer = generateTrainer();
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        trainerService.changePassword("Bilbo.Baggins", "password99");

        assertEquals("password99", trainer.getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    public void shouldActivate() {
        Trainer trainer = generateTrainer();
        trainer.setActive(false);
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        trainerService.activate("Bilbo.Baggins");

        assertTrue(trainer.isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    public void shouldThrowExceptionWhenTrainerIsActivated() {
        Trainer trainer = generateTrainer();
        trainer.setActive(true);

        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        assertThrows(ValidationException.class, () -> trainerService.activate("Bilbo.Baggins"));

        verify(trainerDao, never()).update(any());
    }

    @Test
    public void shouldDeactivate() {
        Trainer trainer = generateTrainer();
        trainer.setActive(true);
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        trainerService.deactivate("Bilbo.Baggins");

        assertFalse(trainer.isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    public void shouldThrowExceptionWhenTrainerIsDeactivated() {
        Trainer trainer = generateTrainer();
        trainer.setActive(false);
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        assertThrows(ValidationException.class, () -> trainerService.deactivate("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnTrueForValidCredentials() {
        Trainer trainer = generateTrainer();
        trainer.setPassword("password12");
        trainer.setActive(true);
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        assertTrue(trainerService.matchCredentials("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldReturnFalseForInvalidCredentials() {
        Trainer trainer = generateTrainer();
        trainer.setPassword("password12");
        trainer.setActive(true);
        when(trainerDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainer));

        assertFalse(trainerService.matchCredentials("Bilbo.Baggins", "password99"));
    }
    
    @Test
    public void shouldReturnTrainersNotAssignedToTrainee() {
        List<Trainer> trainers = List.of(generateTrainer());
        when(trainerDao.findNotAssignedByTrainee("Bilbo.Baggins")).thenReturn(trainers);
        
        assertSame(trainers, trainerService.getTrainersNotAssignedToTrainee("Bilbo.Baggins"));
        
        verify(trainerDao).findNotAssignedByTrainee("Bilbo.Baggins");
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Bilbo");
        trainer.setLastName("Baggins");
        return trainer;
    }

    private TrainingType generateSpecialization() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName(TrainingTypeName.FITNESS);
        return trainingType;
    }
}
