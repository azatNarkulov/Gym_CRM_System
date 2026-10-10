package com.epam.facade;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
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

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee addTrainee(Trainee trainee) {
        return traineeService.add(trainee);
    }

    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        return traineeService.update(username, password, trainee);
    }

    public Trainee getTraineeByUsername(String username, String password) {
        return traineeService.findByUsername(username, password);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void activateTrainee(String username, String password) {
        traineeService.activate(username, password);
    }

    public void deactivateTrainee(String username, String password) {
        traineeService.deactivate(username, password);
    }

    public boolean deleteTraineeByUsername(String username, String password) {
        return traineeService.deleteByUsername(username, password);
    }

    public void updateTraineeTrainers(String traineeUsername, String password, List<String> trainerUsernames) {
        traineeService.updateTrainers(traineeUsername, password, trainerUsernames);
    }

    public Trainer addTrainer(Trainer trainer) {
        return trainerService.add(trainer);
    }

    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        return trainerService.update(username, password, trainer);
    }

    public Trainer getTrainerByUsername(String username, String password) {
        return trainerService.findByUsername(username, password);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public void activateTrainer(String username, String password) {
        trainerService.activate(username, password);
    }

    public void deactivateTrainer(String username, String password) {
        trainerService.deactivate(username, password);
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername, String password) {
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername, password);
    }

    public Training addTraining(String username, String password, Training training) {
        return trainingService.add(username, password, training);
    }

    public List<Training> getTraineeTrainings(String username, String password, LocalDate from, LocalDate to, String trainerName, TrainingTypeName type) {
        TrainingFilterDTO filter = new TrainingFilterDTO(username, from, to, trainerName, type);
        return trainingService.getTraineeTrainings(username, password, filter);
    }

    public List<Training> getTrainerTrainings(String username, String password, LocalDate from, LocalDate to, String traineeName) {
        TrainingFilterDTO filter = new TrainingFilterDTO(username, from, to, traineeName, null);
        return trainingService.getTrainerTrainings(username, password, filter);
    }
}
