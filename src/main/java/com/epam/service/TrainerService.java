package com.epam.service;

import com.epam.domain.Trainer;

import java.util.List;

public interface TrainerService extends UserService<Trainer> {

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);
}
