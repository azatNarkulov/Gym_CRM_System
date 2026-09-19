package com.epam.object;

import com.epam.util.TrainingType;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Training {
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Duration trainingDuration;
    private Long trainingId;
}
