package com.epam.service;

import com.epam.domain.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer add(Trainer trainer);

    Trainer update(Trainer trainer);

    Trainer findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);
}
