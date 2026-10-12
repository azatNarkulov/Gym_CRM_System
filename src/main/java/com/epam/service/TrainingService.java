package com.epam.service;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;

import java.util.List;

public interface TrainingService {

    Training add(String username, String password, Training training);

    List<Training> getTraineeTrainings(String username, String password, TrainingFilterDTO filter);

    List<Training> getTrainerTrainings(String username, String password, TrainingFilterDTO filter);
}
