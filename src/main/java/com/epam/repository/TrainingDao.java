package com.epam.repository;

import com.epam.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    private Map<Long, Training> storage;

    @Autowired
    public void setTrainingMap(@Qualifier("trainingMap") Map<Long, Training> trainingMap) {
        this.storage = trainingMap;
    }

    @Override
    protected Map<Long, Training> getStorage() {
        return storage;
    }
}
