package com.epam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
public class AddTrainingRequest {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @NotBlank(message = "Training name is required")
    private String trainingName;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @DurationMin(seconds = 1, message = "Training duration must be positive")
    private Duration trainingDuration;

    @NotBlank(message = "Training type is required")
    private String trainingType;
}
