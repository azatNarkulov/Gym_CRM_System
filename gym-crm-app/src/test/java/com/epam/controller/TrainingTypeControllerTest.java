package com.epam.controller;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeControllerTest {

    @InjectMocks
    private TrainingTypeController controller;

    @Mock
    private TrainingTypeService trainingTypeService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnAllTrainingTypes() throws Exception {
        TrainingType fitness = new TrainingType();
        fitness.setId(1L);
        fitness.setName(TrainingTypeName.FITNESS);

        TrainingType yoga = new TrainingType();
        yoga.setId(2L);
        yoga.setName(TrainingTypeName.YOGA);

        when(trainingTypeService.getAll()).thenReturn(List.of(fitness, yoga));

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("FITNESS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("YOGA"));
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {
        when(trainingTypeService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
