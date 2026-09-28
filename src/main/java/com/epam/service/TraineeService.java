package com.epam.service;

import com.epam.domain.Trainee;

import java.util.Optional;

public interface TraineeService {

    Trainee add(Trainee trainee);
    Trainee update(Trainee trainee);
    boolean delete(Long id);
    Optional<Trainee> get(Long id);
}
