package com.epam.mapper;

import com.epam.domain.Training;
import com.epam.dto.response.TraineeTrainingResponse;
import com.epam.dto.response.TrainerTrainingResponse;

public class TrainingMapper {
    public static TraineeTrainingResponse toTraineeTraining(Training training) {
        return new TraineeTrainingResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                TrainingTypeMapper.toResponse(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainer().getUsername());
    }

    public static TrainerTrainingResponse toTrainerTraining(Training training) {
        return new TrainerTrainingResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                TrainingTypeMapper.toResponse(training.getTrainingType()),
                training.getTrainingDuration(),
                training.getTrainee().getUsername());
    }
}
