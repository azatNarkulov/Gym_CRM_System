package com.epam.repository;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeDaoTest {

    @InjectMocks
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TrainingType> query;

    @Test
    public void shouldFindByName() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(new TrainingType()));

        assertTrue(trainingTypeDao.findByName(TrainingTypeName.FITNESS).isPresent());
    }

    @Test
    public void shouldReturnEmptyWhenNotFound() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        assertTrue(trainingTypeDao.findByName(TrainingTypeName.FITNESS).isEmpty());
    }

    @Test
    public void shouldGetTrainingById() {
        TrainingType trainingType = new TrainingType();
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(trainingType);

        assertSame(trainingType, trainingTypeDao.get(1L));
    }

    @Test
    public void shouldFindAll() {
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new TrainingType(), new TrainingType()));

        assertEquals(2, trainingTypeDao.findAll().size());
    }
}
