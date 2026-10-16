package com.epam.service;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;
import com.epam.dto.request.AddTrainingRequest;

import java.util.List;

public interface TrainingService {

    Training add(AddTrainingRequest request);

    List<Training> getTraineeTrainings(String username, TrainingFilterDTO filter);

    List<Training> getTrainerTrainings(String username, TrainingFilterDTO filter);
}
