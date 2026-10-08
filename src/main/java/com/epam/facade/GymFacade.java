package com.epam.facade;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.AuthenticationException;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private String currentUsername;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public void login(String username, String password) {
        if (traineeService.matchCredentials(username, password) || trainerService.matchCredentials(username, password)) {
            this.currentUsername = username;
        } else {
            throw new AuthenticationException("Invalid credentials");
        }
    }

    public void logout() {
        this.currentUsername = null;
    }

    public Trainee addTrainee(Trainee trainee) {
        return traineeService.add(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        checkAuthentication();
        return traineeService.update(trainee);
    }

    public boolean deleteTrainee(Long id) {
        checkAuthentication();
        return traineeService.delete(id);
    }

    public Trainee getTraineeByUsername(String username) {
        checkAuthentication();
        return traineeService.findByUsername(username);
    }

    public void changeTraineePassword(String username, String newPassword) {
        checkAuthentication();
        traineeService.changePassword(username, newPassword);
    }

    public void activateTrainee(String username) {
        checkAuthentication();
        traineeService.activate(username);
    }

    public void deactivateTrainee(String username) {
        checkAuthentication();
        traineeService.deactivate(username);
    }

    public boolean deleteTraineeByUsername(String username) {
        checkAuthentication();
        return traineeService.deleteByUsername(username);
    }

    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        checkAuthentication();
        traineeService.updateTrainers(traineeUsername, trainerUsernames);
    }

    public Trainer addTrainer(Trainer trainer) {
        return trainerService.add(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        checkAuthentication();
        return trainerService.update(trainer);
    }

    public Trainer getTrainerByUsername(String username) {
        checkAuthentication();
        return trainerService.findByUsername(username);
    }

    public void changeTrainerPassword(String username, String newPassword) {
        checkAuthentication();
        trainerService.changePassword(username, newPassword);
    }

    public void activateTrainer(String username) {
        checkAuthentication();
        trainerService.activate(username);
    }

    public void deactivateTrainer(String username) {
        checkAuthentication();
        trainerService.deactivate(username);
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        checkAuthentication();
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    public Training addTraining(Training training) {
        checkAuthentication();
        return trainingService.add(training);
    }

    public List<Training> getTraineeTrainings(String username, LocalDate from, LocalDate to, String trainerName, TrainingTypeName type) {
        checkAuthentication();
        return trainingService.getTraineeTrainings(username, from, to, trainerName, type);
    }

    public List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to, String traineeName) {
        checkAuthentication();
        return trainingService.getTrainerTrainings(username, from, to, traineeName);
    }

    private void checkAuthentication() {
        if (currentUsername == null) {
            throw new AuthenticationException("Not authenticated. Call login() first");
        }
    }
}
