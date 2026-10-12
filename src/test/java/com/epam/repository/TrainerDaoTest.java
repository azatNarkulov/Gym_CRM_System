package com.epam.repository;

import com.epam.domain.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerDaoTest {

    @InjectMocks
    private TrainerDao trainerDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainer> trainerQuery;

    @Test
    public void shouldFindNotAssignedByTrainee() {
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter(anyString(), any())).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(new Trainer(), new Trainer()));

        assertEquals(2, trainerDao.findNotAssignedByTrainee("Bilbo.Baggins").size());
    }

    @Test
    public void shouldGetTrainerById() {
        Trainer trainer = new Trainer();
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        assertSame(trainer, trainerDao.get(1L));
    }
}
