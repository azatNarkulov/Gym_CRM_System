package com.epam.service.impl;

import com.epam.domain.Trainer;
import com.epam.exception.ValidationException;
import com.epam.repository.TrainerDao;
import com.epam.service.AbstractUserService;
import com.epam.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    @Autowired
    TrainerDao trainerDao;

    @Override
    public Trainer add(Trainer trainer) {
        validateRequiredSpecialization(trainer);
        return super.add(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        validateRequiredSpecialization(trainer);
        return super.update(trainer);
    }

    private void validateRequiredSpecialization(Trainer trainer) {
        if (trainer.getSpecialization() == null) {
            throw new ValidationException("Specialization is required");
        }
    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerDao.findNotAssignedByTrainee(traineeUsername);
    }
}
