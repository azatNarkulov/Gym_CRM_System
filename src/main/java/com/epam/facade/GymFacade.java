package com.epam.facade;

import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }
}
