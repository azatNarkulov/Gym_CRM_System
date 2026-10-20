package com.epam.repository;

import com.epam.dto.TrainingFilterDTO;
import com.epam.domain.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingDao.class);

    private static final String FIELD_TRAINEE = "trainee";
    private static final String FIELD_TRAINER = "trainer";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_TRAINING_DATE = "trainingDate";
    private static final String FIELD_TRAINING_TYPE = "trainingType";
    private static final String FIELD_NAME = "name";
    private static final String QUERY_FIND_ALL = "SELECT t FROM Training t";

    public List<Training> findByTraineeUsernameAndCriteria(TrainingFilterDTO filter) {
        LOG.debug("Finding trainee by username and criteria");
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Training> query = builder.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(FIELD_TRAINEE).get(FIELD_USERNAME), filter.getUsername()));
        addCommonFilters(builder, filter, root.get(FIELD_TRAINING_DATE),
                root.get(FIELD_TRAINER).get(FIELD_USERNAME), predicates);
        if (filter.getTrainingType() != null) {
            predicates.add(builder.equal(root.get(FIELD_TRAINING_TYPE).get(FIELD_NAME), filter.getTrainingType()));
        }
        return executeQuery(builder, query, predicates);
    }

    public List<Training> findByTrainerUsernameAndCriteria(TrainingFilterDTO filter) {
        LOG.debug("Finding trainer by username and criteria");
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Training> query = builder.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(FIELD_TRAINER).get(FIELD_USERNAME), filter.getUsername()));
        addCommonFilters(builder, filter, root.get(FIELD_TRAINING_DATE),
                root.get(FIELD_TRAINEE).get(FIELD_USERNAME), predicates);
        return executeQuery(builder, query, predicates);
    }

    @Override
    protected Class<Training> getEntityClass() {
        return Training.class;
    }

    @Override
    protected String getFindAllQuery() {
        return QUERY_FIND_ALL;
    }

    private void addCommonFilters(
            CriteriaBuilder builder,
            TrainingFilterDTO filter,
            Path<LocalDate> trainingDate,
            Path<String> otherUsername,
            List<Predicate> predicates) {
        if (filter.getFromDate() != null) {
            predicates.add(builder.greaterThanOrEqualTo(trainingDate, filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            predicates.add(builder.lessThanOrEqualTo(trainingDate, filter.getToDate()));
        }
        if (filter.getOtherUsername() != null && !filter.getOtherUsername().isBlank()) {
            predicates.add(builder.equal(otherUsername, filter.getOtherUsername()));
        }
    }

    private List<Training> executeQuery(
            CriteriaBuilder builder,
            CriteriaQuery<Training> query,
            List<Predicate> predicates) {
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        return getEntityManager().createQuery(query).getResultList();
    }
}
