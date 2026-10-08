package com.epam.repository;

import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao extends AbstractDao<Training> {

    private static final String QUERY_DELETE_BY_TRAINEE_ID = "DELETE FROM Training t WHERE t.trainee.id = :id";

    @Override
    protected Class<Training> getEntityClass() {
        return Training.class;
    }

    public void deleteByTraineeId(Long traineeId) {
        entityManager.createQuery(QUERY_DELETE_BY_TRAINEE_ID)
                .setParameter("id", traineeId)
                .executeUpdate();
    }

    public List<Training> findByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            TrainingTypeName trainingTypeName) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = builder.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("trainee").get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (trainerName != null && !trainerName.isBlank()) {
            String pattern = "%" + trainerName.toLowerCase() + "%";
            predicates.add(builder.or(
                    builder.like(builder.lower(root.get("trainer").get("firstName")), pattern),
                    builder.like(builder.lower(root.get("trainer").get("lastName")), pattern)));
        }
        if (trainingTypeName != null) {
            predicates.add(builder.equal(root.get("trainingType").get("name"), trainingTypeName));
        }

        query.where(builder.and(predicates.toArray(new Predicate[0])));
        query.orderBy(builder.asc(root.get("trainingDate")));

        return entityManager.createQuery(query).getResultList();
    }

    public List<Training> findByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = builder.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("trainer").get("username"), trainerUsername));

        if (fromDate != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (traineeName != null && !traineeName.isBlank()) {
            String pattern = "%" + traineeName.toLowerCase() + "%";
            predicates.add(builder.or(
                    builder.like(builder.lower(root.get("trainee").get("firstName")), pattern),
                    builder.like(builder.lower(root.get("trainee").get("lastName")), pattern)));
        }

        query.where(builder.and(predicates.toArray(new Predicate[0])));
        query.orderBy(builder.asc(root.get("trainingDate")));

        return entityManager.createQuery(query).getResultList();
    }
}
