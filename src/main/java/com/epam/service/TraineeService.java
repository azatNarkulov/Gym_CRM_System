package com.epam.service;

import com.epam.domain.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee add(Trainee trainee);

    Trainee update(Trainee trainee);

    Trainee findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    boolean deleteByUsername(String username);

    void updateTrainers(String traineeUsername, List<String> trainerUsernames);
}
