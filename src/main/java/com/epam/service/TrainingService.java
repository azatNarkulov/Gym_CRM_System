package com.epam.service;

import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training add(Training training);

    Optional<Training> get(Long id);

    List<Training> getTraineeTrainings(String username, LocalDate from, LocalDate to, String trainerName, TrainingTypeName type);

    List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to, String traineeName);
}
