package com.epam.service;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingType> getAll();
    TrainingType findByName(TrainingTypeName name);
}
