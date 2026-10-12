package com.epam.service;

import com.epam.domain.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer add(Trainer trainer);

    Trainer update(String username, String password, Trainer trainer);

    Trainer findByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username, String password);

    void deactivate(String username, String password);

    boolean matchCredentials(String username, String password);

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername, String password);
}
