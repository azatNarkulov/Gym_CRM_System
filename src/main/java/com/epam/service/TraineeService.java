package com.epam.service;

import com.epam.domain.Trainee;

public interface TraineeService extends UserService<Trainee> {

    boolean delete(Long id);
}
