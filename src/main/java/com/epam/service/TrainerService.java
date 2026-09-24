package com.epam.service;

import com.epam.domain.Trainer;
import com.epam.repository.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService extends AbstractUserService<Trainer> {

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.userDao = trainerDao;
    }
}
