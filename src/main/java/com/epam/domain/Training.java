package com.epam.domain;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Training extends BaseEntity {
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Duration trainingDuration;
}
