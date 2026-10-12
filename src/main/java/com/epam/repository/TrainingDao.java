package com.epam.repository;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingDao.class);

    private static final String FIELD_TRAINEE = "trainee";
    private static final String FIELD_TRAINER = "trainer";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_TRAINING_DATE = "trainingDate";
    private static final String FIELD_TRAINING_TYPE = "trainingType";
    private static final String FIELD_NAME = "name";

    public List<Training> findByTraineeUsernameAndCriteria(TrainingFilterDTO filter) {
        LOG.debug("Finding trainee by username and criteria");
        return findByUsernameAndCriteria(FIELD_TRAINEE, FIELD_TRAINER, filter, true);
    }

    public List<Training> findByTrainerUsernameAndCriteria(TrainingFilterDTO filter) {
        LOG.debug("Finding trainer by username and criteria");
        return findByUsernameAndCriteria(FIELD_TRAINER, FIELD_TRAINEE, filter, false);
    }

    private List<Training> findByUsernameAndCriteria(
            String usernameField,
            String otherUsernameField,
            TrainingFilterDTO filter,
            boolean applyTrainingType) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Training> query = builder.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(usernameField).get(FIELD_USERNAME), filter.getUsername()));

        if (filter.getFromDate() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(FIELD_TRAINING_DATE), filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(FIELD_TRAINING_DATE), filter.getToDate()));
        }
        if (filter.getOtherUsername() != null && !filter.getOtherUsername().isBlank()) {
            String pattern = "%" + filter.getOtherUsername().toLowerCase() + "%";
            predicates.add(builder.or(
                    builder.like(builder.lower(root.get(otherUsernameField).get(FIELD_FIRST_NAME)), pattern),
                    builder.like(builder.lower(root.get(otherUsernameField).get(FIELD_LAST_NAME)), pattern)));
        }
        if (applyTrainingType && filter.getTrainingType() != null) {
            predicates.add(builder.equal(root.get(FIELD_TRAINING_TYPE).get(FIELD_NAME), filter.getTrainingType()));
        }

        query.where(builder.and(predicates.toArray(new Predicate[0])));
        query.orderBy(builder.asc(root.get(FIELD_TRAINING_DATE)));

        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    protected Class<Training> getEntityClass() {
        return Training.class;
    }
}
