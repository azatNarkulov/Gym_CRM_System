package com.epam.service;

import com.epam.domain.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee add(Trainee trainee);

    Trainee update(String username, String password, Trainee trainee);

    Trainee findByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username, String password);

    void deactivate(String username, String password);

    boolean matchCredentials(String username, String password);

    boolean deleteByUsername(String username, String password);

    void updateTrainers(String traineeUsername, String password, List<String> trainerUsernames);
}
