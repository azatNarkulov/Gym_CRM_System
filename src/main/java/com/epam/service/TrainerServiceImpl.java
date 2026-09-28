package com.epam.service;

import com.epam.domain.Trainer;
import com.epam.repository.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        setUserDao(trainerDao);
    }
}
