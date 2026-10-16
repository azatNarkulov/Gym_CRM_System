package com.epam.controller;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.request.ActivationRequest;
import com.epam.dto.request.TraineeRegistrationRequest;
import com.epam.dto.request.TrainerUsernameRequest;
import com.epam.dto.request.UpdateTraineeProfileRequest;
import com.epam.dto.request.UpdateTraineeTrainersRequest;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.exception.UserNotFoundException;
import com.epam.filter.AuthenticationFilter;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TraineeControllerTest {

    @InjectMocks
    private TraineeController controller;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

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
    public void shouldReturnCredentialsAfterRegister() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Bilbo");
        request.setLastName("Baggins");

        Trainee created = new Trainee();
        created.setUsername("Bilbo.Baggins");
        created.setPassword("pass123456");

        when(traineeService.add(any(Trainee.class))).thenReturn(created);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Bilbo.Baggins"))
                .andExpect(jsonPath("$.password").value("pass123456"));
    }

    @Test
    public void register_shouldReturn400_whenFirstNameBlank() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("");
        request.setLastName("Baggins");

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnProfile() throws Exception {
        Trainee trainee = generateTrainee();
        when(traineeService.findByUsername("Bilbo.Baggins")).thenReturn(trainee);

        mockMvc.perform(get("/api/trainees/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Bilbo.Baggins"))
                .andExpect(jsonPath("$.firstName").value("Bilbo"));
    }

    @Test
    public void shouldReturn403WhenAccessingAnotherUser() throws Exception {
        mockMvc.perform(get("/api/trainees/Frodo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturn404WhenNotFound() throws Exception {
        when(traineeService.findByUsername("Bilbo.Baggins"))
                .thenThrow(new UserNotFoundException("Bilbo.Baggins"));

        mockMvc.perform(get("/api/trainees/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUpdatedProfile() throws Exception {
        UpdateTraineeProfileRequest request = new UpdateTraineeProfileRequest();
        request.setFirstName("Bilbo");
        request.setLastName("Baggins");
        request.setIsActive(true);

        Trainee existing = generateTrainee();
        when(traineeService.findByUsername("Bilbo.Baggins")).thenReturn(existing);
        when(traineeService.update(any(Trainee.class))).thenReturn(existing);

        mockMvc.perform(put("/api/trainees/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bilbo"));
    }

    @Test
    public void shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/trainees/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotAssignedTrainers() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setUsername("Gandalf.Grey");
        trainer.setFirstName("Gandalf");
        trainer.setLastName("Grey");
        trainer.setSpecialization(generateSpecialization());

        when(trainerService.getTrainersNotAssignedToTrainee("Bilbo.Baggins"))
                .thenReturn(List.of(trainer));

        mockMvc.perform(get("/api/trainees/Bilbo.Baggins/trainers/not-assigned")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Gandalf.Grey"));
    }

    @Test
    public void shouldReturnUpdatedTrainers() throws Exception {
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
         request.setTrainers(List.of(new TrainerUsernameRequest()));

        Trainee trainee = generateTrainee();
        Trainer trainer = new Trainer();
        trainer.setUsername("Gandalf.Grey");
        trainer.setFirstName("Gandalf");
        trainer.setLastName("Grey");
        trainer.setSpecialization(generateSpecialization());
        trainee.setTrainers(java.util.Set.of(trainer));

        when(traineeService.findByUsername("Bilbo.Baggins")).thenReturn(trainee);

        mockMvc.perform(put("/api/trainees/Bilbo.Baggins/trainers")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainers\":[{\"trainerUsername\":\"Gandalf.Grey\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Gandalf.Grey"));
    }

    @Test
    public void shouldReturnTrainings() throws Exception {
        Trainee trainee = generateTrainee();
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(new Trainer());
        training.getTrainer().setUsername("Gandalf.Grey");
        training.setTrainingType(generateSpecialization());
        training.setTrainingName("Yoga");
        training.setTrainingDate(LocalDate.of(2026, 10, 15));
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        when(trainingService.getTraineeTrainings(eq("Bilbo.Baggins"), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainees/Bilbo.Baggins/trainings")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"));
    }

    @Test
    public void shouldReturn200WhenSetActive() throws Exception {
        ActivationRequest request = new ActivationRequest();
        request.setIsActive(false);

        mockMvc.perform(patch("/api/trainees/Bilbo.Baggins/activate")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400WhenDateParamInvalid() throws Exception {
        mockMvc.perform(get("/api/trainees/Bilbo.Baggins/trainings")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .param("fromDate", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn500ForUnexpectedException() throws Exception {
        when(traineeService.findByUsername("Bilbo.Baggins"))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/trainees/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isInternalServerError());
    }

    private Trainee generateTrainee() {
        Trainee t = new Trainee();
        t.setUsername("Bilbo.Baggins");
        t.setFirstName("Bilbo");
        t.setLastName("Baggins");
        t.setActive(true);
        return t;
    }

    private TrainingType generateSpecialization() {
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.FITNESS);
        return type;
    }
}
