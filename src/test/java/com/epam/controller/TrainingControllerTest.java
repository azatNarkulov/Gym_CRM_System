package com.epam.controller;

import com.epam.domain.Training;
import com.epam.dto.request.AddTrainingRequest;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @InjectMocks
    private TrainingController controller;

    @Mock
    private TrainingService trainingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    public void shouldReturn200WhenAddTraining() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Bilbo.Baggins");
        request.setTrainerUsername("Gandalf.Grey");
        request.setTrainingName("Yoga");
        request.setTrainingDate(LocalDate.of(2026, 10, 15));
        request.setTrainingDuration(Duration.ofHours(1));
        request.setTrainingType("FITNESS");

        when(trainingService.add(any(AddTrainingRequest.class))).thenReturn(new Training());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400WhenRequiredFieldMissing() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
