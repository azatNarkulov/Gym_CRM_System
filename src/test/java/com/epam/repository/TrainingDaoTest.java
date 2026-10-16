package com.epam.repository;

import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.TrainingFilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingDaoTest {

    @InjectMocks
    private TrainingDao trainingDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Training> criteriaQuery;

    @Mock
    private Root<Training> root;

    @Mock
    private Predicate predicate;

    @Mock
    private TypedQuery<Training> typedQuery;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFindByTraineeUsernameAndCriteriaWithAllFilters() {
        TrainingFilterDTO filter = new TrainingFilterDTO(
                "Bilbo.Baggins",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                "Gandalf.Grey",
                TrainingTypeName.FITNESS);

        stubCriteria();
        when(criteriaBuilder.greaterThanOrEqualTo(any(Path.class), any(LocalDate.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Path.class), any(LocalDate.class))).thenReturn(predicate);
        when(typedQuery.getResultList()).thenReturn(List.of(new Training()));

        List<Training> result = trainingDao.findByTraineeUsernameAndCriteria(filter);

        assertEquals(1, result.size());
    }

    @Test
    public void shouldFindByTraineeUsernameAndCriteriaWithoutOptionalFilters() {
        TrainingFilterDTO filter = new TrainingFilterDTO("Bilbo.Baggins", null, null, null, null);

        stubCriteria();
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<Training> result = trainingDao.findByTraineeUsernameAndCriteria(filter);

        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFindByTrainerUsernameAndCriteria() {
        TrainingFilterDTO filter = new TrainingFilterDTO(
                "Gandalf.Grey",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                "Bilbo.Baggins",
                null);

        stubCriteria();
        when(criteriaBuilder.greaterThanOrEqualTo(any(Path.class), any(LocalDate.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Path.class), any(LocalDate.class))).thenReturn(predicate);
        when(typedQuery.getResultList()).thenReturn(List.of(new Training(), new Training()));

        List<Training> result = trainingDao.findByTrainerUsernameAndCriteria(filter);

        assertEquals(2, result.size());
    }

    @SuppressWarnings("unchecked")
    private void stubCriteria() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Training.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Training.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn((Path) root);
        when(criteriaBuilder.equal(any(Expression.class), any(Object.class))).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
    }
}
