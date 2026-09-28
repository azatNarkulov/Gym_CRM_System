package com.epam.service;

import com.epam.domain.Training;

import java.util.Optional;

public interface TrainingService {

    Training add(Training training);
    Optional<Training> get(Long id);
}
