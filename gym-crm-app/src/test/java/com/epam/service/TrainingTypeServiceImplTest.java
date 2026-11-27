package com.epam.service;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.TrainingTypeNotFoundException;
import com.epam.repository.TrainingTypeDao;
import com.epam.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceImplTest {

    @InjectMocks
    private TrainingTypeServiceImpl service;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Test
    public void shouldReturnAll() {
        List<TrainingType> types = List.of(new TrainingType());
        when(trainingTypeDao.findAll()).thenReturn(types);

        assertSame(types, service.getAll());
    }

    @Test
    public void shouldFindByName() {
        TrainingType type = new TrainingType();
        when(trainingTypeDao.findByName(TrainingTypeName.FITNESS)).thenReturn(Optional.of(type));

        assertSame(type, service.findByName(TrainingTypeName.FITNESS));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        when(trainingTypeDao.findByName(TrainingTypeName.FITNESS)).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () -> service.findByName(TrainingTypeName.FITNESS));
    }
}
