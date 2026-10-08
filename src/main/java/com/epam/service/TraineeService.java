package com.epam.service;

import com.epam.domain.Trainee;

import java.util.List;

public interface TraineeService extends UserService<Trainee> {

    boolean delete(Long id);

    boolean deleteByUsername(String username);

    void updateTrainers(String traineeUsername, List<String> trainerUsernames);
}
