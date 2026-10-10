package com.epam.facade;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    public void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    public void shouldAddTrainee() {
        Trainee trainee = new Trainee();
        when(traineeService.add(trainee)).thenReturn(trainee);

        assertSame(trainee, gymFacade.addTrainee(trainee));
        verify(traineeService).add(trainee);
    }

    @Test
    public void shouldUpdateTrainee() {
        Trainee trainee = new Trainee();
        when(traineeService.update("Bilbo.Baggins", "password12", trainee)).thenReturn(trainee);

        assertSame(trainee, gymFacade.updateTrainee("Bilbo.Baggins", "password12", trainee));
        verify(traineeService).update("Bilbo.Baggins", "password12", trainee);
    }

    @Test
    public void shouldGetTraineeByUsername() {
        Trainee trainee = new Trainee();
        when(traineeService.findByUsername("Bilbo.Baggins", "password12")).thenReturn(trainee);

        assertSame(trainee, gymFacade.getTraineeByUsername("Bilbo.Baggins", "password12"));
        verify(traineeService).findByUsername("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldDeleteTraineeByUsername() {
        String username = "Bilbo.Baggins";
        String password = "password12";
        when(traineeService.deleteByUsername(username, password)).thenReturn(true);

        assertTrue(gymFacade.deleteTraineeByUsername(username, password));
        verify(traineeService).deleteByUsername(username, password);
    }

    @Test
    public void shouldChangeTraineePassword() {
        gymFacade.changeTraineePassword("Bilbo.Baggins", "password12", "password99");
        verify(traineeService).changePassword("Bilbo.Baggins", "password12", "password99");
    }

    @Test
    public void shouldActivateAndDeactivateTrainee() {
        String username = "Bilbo.Baggins";
        String password = "password12";
        gymFacade.activateTrainee(username, password);
        gymFacade.deactivateTrainee(username, password);

        verify(traineeService).activate(username, password);
        verify(traineeService).deactivate(username, password);
    }

    @Test
    public void shouldUpdateTraineeTrainers() {
        List<String> trainerUsernames = List.of("Frodo.Baggins", "Remy.Trainer");

        gymFacade.updateTraineeTrainers("Bilbo.Baggins", "password12", trainerUsernames);

        verify(traineeService).updateTrainers("Bilbo.Baggins", "password12", trainerUsernames);
    }

    @Test
    public void shouldAddTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.add(trainer)).thenReturn(trainer);

        assertSame(trainer, gymFacade.addTrainer(trainer));
        verify(trainerService).add(trainer);
    }

    @Test
    public void shouldUpdateTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.update("Bilbo.Baggins", "password12", trainer)).thenReturn(trainer);

        assertSame(trainer, gymFacade.updateTrainer("Bilbo.Baggins", "password12", trainer));
        verify(trainerService).update("Bilbo.Baggins", "password12", trainer);
    }

    @Test
    public void shouldGetTrainerByUsername() {
        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("Bilbo.Baggins", "password12")).thenReturn(trainer);

        assertSame(trainer, gymFacade.getTrainerByUsername("Bilbo.Baggins", "password12"));
        verify(trainerService).findByUsername("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldChangeTrainerPassword() {
        gymFacade.changeTrainerPassword("Bilbo.Baggins", "password12", "password99");
        verify(trainerService).changePassword("Bilbo.Baggins", "password12", "password99");
    }

    @Test
    public void shouldActivateAndDeactivateTrainer() {
        gymFacade.activateTrainer("Bilbo.Baggins", "password12");
        gymFacade.deactivateTrainer("Bilbo.Baggins", "password12");

        verify(trainerService).activate("Bilbo.Baggins", "password12");
        verify(trainerService).deactivate("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldGetTrainersNotAssignedToTrainee() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.getTrainersNotAssignedToTrainee("Bilbo.Baggins", "password12")).thenReturn(trainers);

        assertSame(trainers, gymFacade.getTrainersNotAssignedToTrainee("Bilbo.Baggins", "password12"));
        verify(trainerService).getTrainersNotAssignedToTrainee("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldAddTraining() {
        Training training = new Training();
        when(trainingService.add("Bilbo.Baggins", "password12", training)).thenReturn(training);

        assertSame(training, gymFacade.addTraining("Bilbo.Baggins", "password12", training));
        verify(trainingService).add("Bilbo.Baggins", "password12", training);
    }

    @Test
    public void shouldGetTraineeTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(new Training());
        TrainingFilterDTO filter = new TrainingFilterDTO("Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS);

        when(trainingService.getTraineeTrainings("Bilbo.Baggins", "password12", filter)).thenReturn(trainings);

        assertSame(trainings, gymFacade.getTraineeTrainings("Bilbo.Baggins", "password12", from, to, "Trainer", TrainingTypeName.FITNESS));

        verify(trainingService).getTraineeTrainings("Bilbo.Baggins", "password12", filter);
    }

    @Test
    public void shouldGetTrainerTrainings() {
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(new Training());
        TrainingFilterDTO filter = new TrainingFilterDTO("Bilbo.Baggins", from, to, "Trainee", null);

        when(trainingService.getTrainerTrainings("Bilbo.Baggins", "password12", filter))
                .thenReturn(trainings);

        assertSame(trainings, gymFacade.getTrainerTrainings("Bilbo.Baggins", "password12", from, to, "Trainee"));

        verify(trainingService).getTrainerTrainings("Bilbo.Baggins", "password12", filter);
    }
}
