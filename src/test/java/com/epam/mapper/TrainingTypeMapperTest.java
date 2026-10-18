package com.epam.mapper;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.response.TrainingTypeResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrainingTypeMapperTest {

    @Test
    void shouldReturnNullForNullInput() {
        TrainingTypeResponse response = TrainingTypeMapper.toResponse(null);
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
    }

    @Test
    void shouldMapToResponse() {
        TrainingType type = new TrainingType();
        type.setId(5L);
        type.setName(TrainingTypeName.YOGA);

        TrainingTypeResponse response = TrainingTypeMapper.toResponse(type);

        assertEquals(5L, response.getId());
        assertEquals("YOGA", response.getName());
    }
}
