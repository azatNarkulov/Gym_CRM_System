package com.epam.actuator;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.repository.TrainingTypeDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypesHealthIndicatorTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypesHealthIndicator healthIndicator;

    @Test
    public void shouldReturnUpWhenAllTrainingTypesExist() {
        List<TrainingType> types = Arrays.stream(TrainingTypeName.values())
                        .map(name -> {
                            TrainingType type = new TrainingType();
                            type.setName(name);
                            return type;
                        })
                        .toList();

        when(trainingTypeDao.findAll()).thenReturn(types);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    public void shouldReturnDownWhenTrainingTypesAreEmpty() {
        when(trainingTypeDao.findAll()).thenReturn(Collections.emptyList());

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void shouldReturnDownWhenDaoThrowException() {
        when(trainingTypeDao.findAll()).thenThrow(new RuntimeException("DB Error"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
