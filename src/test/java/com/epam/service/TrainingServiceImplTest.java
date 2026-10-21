package com.epam.service;

import com.epam.actuator.GymMetrics;
import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.request.AddTrainingRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserService userService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private GymMetrics gymMetrics;

    @Test
    public void shouldPersistTraining() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType specialization = generateTrainingType();
        trainer.setSpecialization(specialization);

        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Bilbo.Baggins");
        request.setTrainerUsername("Gandalf.Grey");
        request.setTrainingName("Saturday Fitness");
        request.setTrainingDate(LocalDate.of(2026, 10, 15));
        request.setTrainingDuration(Duration.ofHours(1));
        request.setTrainingType("FITNESS");

        when(userService.findByUsername("Bilbo.Baggins")).thenReturn(trainee);
        when(userService.findByUsername("Gandalf.Grey")).thenReturn(trainer);
        when(trainingTypeService.findByName(TrainingTypeName.FITNESS)).thenReturn(specialization);
        when(trainingDao.add(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training result = trainingService.add(request);

        assertNotNull(result);
        assertSame(trainee, result.getTrainee());
        assertSame(trainer, result.getTrainer());
        assertSame(specialization, result.getTrainingType());
        assertEquals("Saturday Fitness", result.getTrainingName());
        verify(trainingDao).add(any(Training.class));
    }
    
    @Test
    public void shouldReturnTraineeTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(generateTraining());

        TrainingFilterDTO filter = new TrainingFilterDTO("Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS);
        
        when(trainingDao.findByTraineeUsernameAndCriteria(filter)).thenReturn(trainings);
        
        List<Training> result = trainingService.getTraineeTrainings("Bilbo.Baggins", filter);
        
        assertSame(trainings, result);
        verify(trainingDao).findByTraineeUsernameAndCriteria(filter);
    }
    
    @Test
    public void shouldReturnTrainerTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(generateTraining());

        TrainingFilterDTO filter = new TrainingFilterDTO("Bilbo.Baggins", from, to, "Trainee", null);

        when(trainingDao.findByTrainerUsernameAndCriteria(filter)).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("Bilbo.Baggins", filter);

        assertSame(trainings, result);
        verify(trainingDao).findByTrainerUsernameAndCriteria(filter);
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
