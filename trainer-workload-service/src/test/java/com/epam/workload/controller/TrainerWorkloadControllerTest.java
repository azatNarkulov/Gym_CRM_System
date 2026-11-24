package com.epam.workload.controller;

import com.epam.workload.dto.response.WorkloadSummaryResponse;
import com.epam.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainerWorkloadControllerTest {

    private TrainerWorkloadService service;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        service = mock(TrainerWorkloadService.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor methodValidation = new MethodValidationPostProcessor();
        methodValidation.setValidator(validator);
        methodValidation.afterPropertiesSet();
        TrainerWorkloadController controller = new TrainerWorkloadController(service);
        TrainerWorkloadController validatedController =
                (TrainerWorkloadController) methodValidation.postProcessAfterInitialization(
                        controller, "trainerWorkloadController");
        mockMvc = MockMvcBuilders.standaloneSetup(validatedController)
                .setControllerAdvice(new com.epam.workload.exception.WorkloadExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    public void shouldAcceptWorkloadUpdateAndReturn200() throws Exception {
        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainerUsername": "trainer.username",
                                  "trainerFirstName": "First",
                                  "trainerLastName": "Last",
                                  "isActive": true,
                                  "trainingDate": "2026-11-20",
                                  "trainingDuration": "PT1H30M",
                                  "actionType": "ADD"
                                }
                                """))
                .andExpect(status().isOk());

        verify(service).updateWorkload(any());
    }

    @Test
    public void shouldRejectInvalidWorkloadUpdate() throws Exception {
        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainerUsername": "",
                                  "trainerFirstName": "First",
                                  "trainerLastName": "Last",
                                  "isActive": true,
                                  "trainingDate": "2026-11-20",
                                  "trainingDuration": "PT0S",
                                  "actionType": "ADD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRejectUnsupportedWorkloadAction() throws Exception {
        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainerUsername": "trainer.username",
                                  "trainerFirstName": "First",
                                  "trainerLastName": "Last",
                                  "isActive": true,
                                  "trainingDate": "2026-11-20",
                                  "trainingDuration": "PT1H",
                                  "actionType": "UPDATE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnMonthlyWorkloadInHours() throws Exception {
        when(service.getMonthlyWorkload("trainer.username", 2026, 11))
                .thenReturn(new WorkloadSummaryResponse(
                        "trainer.username",
                        "First",
                        "Last",
                        true,
                        List.of(new WorkloadSummaryResponse.YearSummary(
                                2026,
                                List.of(new WorkloadSummaryResponse.MonthSummary(11, new BigDecimal("1.5")))))));

        mockMvc.perform(get("/api/trainers/trainer.username/workload")
                        .param("year", "2026")
                        .param("month", "11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("trainer.username"))
                .andExpect(jsonPath("$.years[0].year").value(2026))
                .andExpect(jsonPath("$.years[0].months[0].month").value(11))
                .andExpect(jsonPath("$.years[0].months[0].trainingSummaryDurationHours").value(1.5));
    }

    @Test
    public void shouldRejectMonthOutsideCalendarRange() throws Exception {
        mockMvc.perform(get("/api/trainers/trainer.username/workload")
                        .param("year", "2026")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRejectNonNumericYear() throws Exception {
        mockMvc.perform(get("/api/trainers/trainer.username/workload")
                        .param("year", "not-a-year")
                        .param("month", "11"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRejectMissingMonth() throws Exception {
        mockMvc.perform(get("/api/trainers/trainer.username/workload")
                        .param("year", "2026"))
                .andExpect(status().isBadRequest());
    }
}
