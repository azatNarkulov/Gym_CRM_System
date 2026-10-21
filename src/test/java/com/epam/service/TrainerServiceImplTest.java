package com.epam.service;

import com.epam.actuator.GymMetrics;
import com.epam.domain.Trainer;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainerDao;
import com.epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserService userService;

    @Mock
    private GymMetrics gymMetrics;

    @Test
    public void shouldCreateTrainer() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(generateSpecialization());

        when(trainerDao.add(trainer)).thenReturn(trainer);

        assertSame(trainer, trainerService.add(trainer));
        verify(userService).prepareForCreation(trainer);
        verify(trainerDao).add(trainer);
    }

    @Test
    public void shouldThrowExceptionWhenAddWithoutSpecialization() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(null);

        assertThrows(ValidationException.class, () -> trainerService.add(trainer));
        verifyNoInteractions(trainerDao, userService);
    }

    @Test
    public void shouldDelegateUpdate() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(generateSpecialization());
        when(userService.update(trainer)).thenReturn(trainer);

        assertSame(trainer, trainerService.update(trainer));
        verify(userService).update(trainer);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingWithoutSpecialization() {
        Trainer trainer = generateTrainer();
        trainer.setSpecialization(null);

        assertThrows(ValidationException.class, () -> trainerService.update(trainer));
    }

    @Test
    public void shouldDelegateFindByUsername() {
        Trainer trainer = generateTrainer();
        when(userService.findByUsername("Bilbo.Baggins")).thenReturn(trainer);

        assertSame(trainer, trainerService.findByUsername("Bilbo.Baggins"));
        verify(userService).findByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldDelegateChangePassword() {
        trainerService.changePassword("Bilbo.Baggins", "password12", "password99");
        verify(userService).changePassword("Bilbo.Baggins", "password12", "password99");
    }

    @Test
    public void shouldGetTrainersNotAssignedToTrainee() {
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
