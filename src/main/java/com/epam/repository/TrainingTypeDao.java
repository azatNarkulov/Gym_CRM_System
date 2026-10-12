package com.epam.repository;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingTypeDao extends AbstractDao<TrainingType> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingTypeDao.class);

    private static final String QUERY_FIND_BY_NAME = "SELECT t FROM TrainingType t WHERE t.name = :name";

    public Optional<TrainingType> findByName(TrainingTypeName name) {
        LOG.debug("Finding Training type={}", name);
        return getEntityManager()
                .createQuery(QUERY_FIND_BY_NAME, TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();

    }

    @Override
    protected Class<TrainingType> getEntityClass() {
        return TrainingType.class;
    }
}
