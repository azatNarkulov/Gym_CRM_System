package com.epam.dto.request;

import com.epam.domain.Trainer;
import com.epam.domain.Training;

import java.time.Duration;
import java.time.LocalDate;

public record WorkloadUpdateRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean isActive,
        LocalDate trainingDate,
        Duration trainingDuration,
        WorkloadAction actionType) {

    public static WorkloadUpdateRequest from(Trainer trainer, Training training, WorkloadAction action) {
        return new WorkloadUpdateRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                action);
    }
}
