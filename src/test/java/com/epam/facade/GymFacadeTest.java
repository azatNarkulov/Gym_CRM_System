package com.epam.facade;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.AuthenticationException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void shouldLoginAsTrainee() {
        when(traineeService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(true);

        gymFacade.login("Bilbo.Baggins", "password12");

        gymFacade.getTraineeByUsername("Bilbo.Baggins");
        verify(traineeService).findByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldLoginAsTrainer() {
        when(trainerService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(true);

        gymFacade.login("Bilbo.Baggins", "password12");

        gymFacade.getTrainerByUsername("Bilbo.Baggins");
        verify(trainerService).findByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldThrowExceptionForInvalidCredentials() {
        when(traineeService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(false);
        when(trainerService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> gymFacade.login("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldRequireAuthenticationForProtectedMethods() {
        assertThrows(AuthenticationException.class, () -> gymFacade.getTraineeByUsername("Bilbo.Baggins"));
        assertThrows(AuthenticationException.class, () -> gymFacade.updateTrainee(new Trainee()));
        assertThrows(AuthenticationException.class, () -> gymFacade.deleteTrainee(1L));
        assertThrows(AuthenticationException.class, () -> gymFacade.changeTraineePassword("Bilbo.Baggins", "password12"));
        assertThrows(AuthenticationException.class, () -> gymFacade.activateTrainee("Bilbo.Baggins"));
        assertThrows(AuthenticationException.class, () -> gymFacade.getTrainerByUsername("Bilbo.Baggins"));
        assertThrows(AuthenticationException.class, () -> gymFacade.addTraining(null));
    }

    @Test
    public void shouldRequireNewLoginAfterLogout() {
        when(traineeService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(true);
        gymFacade.login("Bilbo.Baggins", "password12");
        gymFacade.logout();

        assertThrows(AuthenticationException.class, () -> gymFacade.getTraineeByUsername("Bilbo.Baggins"));
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
        authenticate();
        Trainee trainee = new Trainee();
        when(traineeService.update(trainee)).thenReturn(trainee);

        assertSame(trainee, gymFacade.updateTrainee(trainee));
        verify(traineeService).update(trainee);
    }

    @Test
    public void shouldGetTraineeByUsername() {
        authenticate();
        Trainee trainee = new Trainee();
        when(traineeService.findByUsername("Bilbo.Baggins")).thenReturn(trainee);

        assertSame(trainee, gymFacade.getTraineeByUsername("Bilbo.Baggins"));
        verify(traineeService).findByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldDeleteTraineeById() {
        authenticate();
        when(traineeService.delete(1L)).thenReturn(true);

        assertTrue(gymFacade.deleteTrainee(1L));
        verify(traineeService).delete(1L);
    }

    @Test
    public void shouldDeleteTraineeByUsername() {
        authenticate();
        when(traineeService.deleteByUsername("Bilbo.Baggins")).thenReturn(true);

        assertTrue(gymFacade.deleteTraineeByUsername("Bilbo.Baggins"));
        verify(traineeService).deleteByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldChangeTraineePassword() {
        authenticate();
        gymFacade.changeTraineePassword("Bilbo.Baggins", "password99");
        verify(traineeService).changePassword("Bilbo.Baggins", "password99");
    }

    @Test
    public void shouldActivateAndDeactivateTrainee() {
        authenticate();
        gymFacade.activateTrainee("Bilbo.Baggins");
        gymFacade.deactivateTrainee("Bilbo.Baggins");

        verify(traineeService).activate("Bilbo.Baggins");
        verify(traineeService).deactivate("Bilbo.Baggins");
    }

    @Test
    public void shouldUpdateTraineeTrainers() {
        authenticate();
        List<String> trainerUsernames = List.of("Frodo.Baggins", "Remy.Trainer");

        gymFacade.updateTraineeTrainers("Bilbo.Baggins", trainerUsernames);

        verify(traineeService).updateTrainers("Bilbo.Baggins", trainerUsernames);
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
        authenticate();
        Trainer trainer = new Trainer();
        when(trainerService.update(trainer)).thenReturn(trainer);

        assertSame(trainer, gymFacade.updateTrainer(trainer));
        verify(trainerService).update(trainer);
    }

    @Test
    public void shouldGetTrainerByUsername() {
        authenticate();
        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("Bilbo.Baggins")).thenReturn(trainer);

        assertSame(trainer, gymFacade.getTrainerByUsername("Bilbo.Baggins"));
        verify(trainerService).findByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldChangeTrainerPassword() {
        authenticate();
        gymFacade.changeTrainerPassword("Bilbo.Baggins", "password99");
        verify(trainerService).changePassword("Bilbo.Baggins", "password99");
    }

    @Test
    public void shouldActivateAndDeactivateTrainer() {
        authenticate();
        gymFacade.activateTrainer("Bilbo.Baggins");
        gymFacade.deactivateTrainer("Bilbo.Baggins");

        verify(trainerService).activate("Bilbo.Baggins");
        verify(trainerService).deactivate("Bilbo.Baggins");
    }

    @Test
    public void shouldGetTrainersNotAssignedToTrainee() {
        authenticate();
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.getTrainersNotAssignedToTrainee("Bilbo.Baggins")).thenReturn(trainers);

        assertSame(trainers, gymFacade.getTrainersNotAssignedToTrainee("Bilbo.Baggins"));
        verify(trainerService).getTrainersNotAssignedToTrainee("Bilbo.Baggins");
    }

    @Test
    public void shouldAddTraining() {
        authenticate();
        Training training = new Training();
        when(trainingService.add(training)).thenReturn(training);

        assertSame(training, gymFacade.addTraining(training));
        verify(trainingService).add(training);
    }

    @Test
    public void shouldGetTraineeTrainings() {
        authenticate();
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(new Training());

        when(trainingService.getTraineeTrainings("Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS))
                .thenReturn(trainings);

        assertSame(trainings, gymFacade.getTraineeTrainings("Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS));

        verify(trainingService).getTraineeTrainings("Bilbo.Baggins", from, to, "Trainer", TrainingTypeName.FITNESS);
    }

    @Test
    public void shouldGetTrainerTrainings() {
        authenticate();
        LocalDate from = LocalDate.of(2026, 3, 3);
        LocalDate to = LocalDate.of(2026, 12, 14);
        List<Training> trainings = List.of(new Training());

        when(trainingService.getTrainerTrainings("Bilbo.Baggins", from, to, "Trainee"))
                .thenReturn(trainings);

        assertSame(trainings, gymFacade.getTrainerTrainings("Bilbo.Baggins", from, to, "Trainee"));

        verify(trainingService).getTrainerTrainings("Bilbo.Baggins", from, to, "Trainee");
    }

    private void authenticate() {
        when(traineeService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(true);
        gymFacade.login("Bilbo.Baggins", "password12");
    }
}
