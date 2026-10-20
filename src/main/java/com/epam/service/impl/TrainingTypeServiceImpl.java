package com.epam.service.impl;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.TrainingTypeNotFoundException;
import com.epam.repository.TrainingTypeDao;
import com.epam.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingTypeServiceImpl implements TrainingTypeService {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Override
    public List<TrainingType> getAll() {
        return trainingTypeDao.findAll();
    }

    @Override
    public TrainingType findByName(TrainingTypeName name) {
        return trainingTypeDao.findByName(name)
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training type with name " + name + " not found"));
    }
}
