package com.epam.service;

import com.epam.domain.Trainer;

import java.util.Optional;

public interface TrainerService {

    Trainer add(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> get(Long id);
}
