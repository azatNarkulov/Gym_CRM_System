package com.epam.repository;

import com.epam.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainerDao extends AbstractUserDao<Trainer> {

    private Map<Long, Trainer> storage;

    @Autowired
    public void setTrainerMap(@Qualifier("trainerMap") Map<Long, Trainer> trainerMap) {
        this.storage = trainerMap;
    }

    @Override
    protected Map<Long, Trainer> getStorage() {
        return storage;
    }
}
