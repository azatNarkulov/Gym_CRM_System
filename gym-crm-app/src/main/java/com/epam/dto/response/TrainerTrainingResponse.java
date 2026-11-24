package com.epam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TrainerTrainingResponse {
    private String trainingName;
    private LocalDate trainingDate;
    private TrainingTypeResponse trainingType;
    private Duration trainingDuration;
    private String traineeName;
}
