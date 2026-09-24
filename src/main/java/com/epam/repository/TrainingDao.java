package com.epam.repository;

import com.epam.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    @Autowired
    public void setTrainingMap(@Qualifier("trainingMap") Map<Long, Training> trainingMap) {
        setStorage(trainingMap);
    }
}
