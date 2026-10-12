package com.epam.repository;

import com.epam.domain.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeDaoTest {

    @InjectMocks
    private TraineeDao traineeDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query traineeQuery;

    @Mock
    private Query userQuery;

    @Test
    public void shouldReturnFalseWhenDeleteForNullOrBlank() {
        assertFalse(traineeDao.delete(null));
        assertFalse(traineeDao.delete(""));
        assertFalse(traineeDao.delete("   "));
    }

    @Test
    public void shouldUseBothQueriesWhenDelete() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(traineeQuery, userQuery);
        when(traineeQuery.setParameter(anyString(), any())).thenReturn(traineeQuery);
        when(userQuery.setParameter(anyString(), any())).thenReturn(userQuery);
        when(traineeQuery.executeUpdate()).thenReturn(1);
        when(userQuery.executeUpdate()).thenReturn(1);

        assertTrue(traineeDao.delete("Bilbo.Baggins"));

        verify(traineeQuery).executeUpdate();
        verify(userQuery).executeUpdate();
    }

    @Test
    public void shouldReturnFalseWhenDeleteAndUserNotFound() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(traineeQuery, userQuery);
        when(traineeQuery.setParameter(anyString(), any())).thenReturn(traineeQuery);
        when(userQuery.setParameter(anyString(), any())).thenReturn(userQuery);
        when(traineeQuery.executeUpdate()).thenReturn(0);
        when(userQuery.executeUpdate()).thenReturn(0);

        assertFalse(traineeDao.delete("Frodo.Baggins"));
    }

    @Test
    public void shouldGetTraineeById() {
        Trainee trainee = new Trainee();
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        assertSame(trainee, traineeDao.get(1L));
    }
}
