package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainingDao;
import com.epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingDao trainingDao;

    @Test
        public void shouldPersistTraining() {
        Training training = generateTraining();
        when(trainingDao.add(training)).thenReturn(training);

        Training result = trainingService.add(training);

        assertSame(training, result);
        verify(trainingDao).add(training);
    }
    
    @Test
    public void shouldThrowExceptionWhenTraineeIsNull() {
        Training training = generateTraining();
        training.setTrainee(null);
        
        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldThrowExceptionWhenTrainerIsNull() {
        Training training = generateTraining();
        training.setTrainer(null);

        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldThrowExceptionWhenTrainingTypeIsNull() {
        Training training = generateTraining();
        training.setTrainingType(null);

        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldThrowExceptionWhenTrainingNameIsBlank() {
        Training training = generateTraining();
        training.setTrainingName(" ");

        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldThrowExceptionWhenTrainingDateIsNull() {
        Training training = generateTraining();
        training.setTrainingDate(null);

        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldThrowExceptionWhenTrainingDurationIsNull() {
        Training training = generateTraining();
        training.setTrainingDuration(null);

        assertThrows(ValidationException.class, () -> trainingService.add(training));
        verifyNoInteractions(trainingDao);
    }

    @Test
    public void shouldGetTraining() {
        Training training = generateTraining();
        when(trainingDao.get(1L)).thenReturn(training);

        assertEquals(Optional.of(training), trainingService.get(1L));
    }

    @Test
    public void shouldReturnEmptyWhenTrainingDoesNotExist() {
        when(trainingDao.get(1L)).thenReturn(null);
        assertTrue(trainingService.get(1L).isEmpty());
    }
    
    @Test
    public void shouldReturnTraineeTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(generateTraining());
        
        when(trainingDao.findByTraineeUsernameAndCriteria(
                "Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS
        )).thenReturn(trainings);
        
        List<Training> result = trainingService.getTraineeTrainings(
                "Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS
        );
        
        assertSame(trainings, result);
        verify(trainingDao).findByTraineeUsernameAndCriteria(
                "Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS
        );
    }
    
    @Test
    public void shouldReturnTrainerTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(generateTraining());

        when(trainingDao.findByTrainerUsernameAndCriteria("Bilbo.Baggins", from, to, "Trainee"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("Bilbo.Baggins", from, to, "Trainee");

        assertSame(trainings, result);
        verify(trainingDao).findByTrainerUsernameAndCriteria("Bilbo.Baggins", from, to, "Trainee");
    }

    private Training generateTraining() {
        Training training = new Training();
        training.setTrainee(new Trainee());
        training.setTrainer(new Trainer());
        training.setTrainingType(generateTrainingType());
        training.setTrainingName("Saturday Fitness");
        training.setTrainingDate(LocalDate.of(2026, 10, 7));
        training.setTrainingDuration(Duration.ofHours(1));
        return training;
    }
    
    private TrainingType generateTrainingType() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName(TrainingTypeName.FITNESS);
        return trainingType;
    }
}
