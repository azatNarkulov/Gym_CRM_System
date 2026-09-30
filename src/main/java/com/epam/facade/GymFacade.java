package com.epam.facade;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public Optional<Trainee> getTrainee(Long id) {
        return traineeService.get(id);
    }

    public boolean deleteTrainee(Long id) {
        return traineeService.delete(id);
    }

    public Trainer addTrainer(Trainer trainer) {
        return trainerService.add(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Optional<Trainer> getTrainer(Long id) {
        return trainerService.get(id);
    }

    public Training addTraining(Training training) {
        return trainingService.add(training);
    }

    public Optional<Training> getTraining(Long id) {
        return trainingService.get(id);
    }
}
