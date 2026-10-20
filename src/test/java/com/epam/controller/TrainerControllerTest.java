package com.epam.controller;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.request.ActivationRequest;
import com.epam.dto.request.TrainerRegistrationRequest;
import com.epam.dto.request.UpdateTrainerProfileRequest;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.exception.UserNotFoundException;
import com.epam.filter.AuthenticationFilter;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import com.epam.service.TrainingTypeService;
import com.epam.service.UserService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrainerControllerTest {

    @InjectMocks
    private TrainerController controller;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private UserService userService;

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
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Bilbo");
        request.setLastName("Baggins");
        request.setSpecialization(TrainingTypeName.FITNESS.toString());

        Trainer created = new Trainer();
        created.setUsername("Bilbo.Baggins");
        created.setPassword("password12");

        when(trainingTypeService.findByName(TrainingTypeName.FITNESS)).thenReturn(generateSpecialization());
        when(trainerService.add(any(Trainer.class))).thenReturn(created);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Bilbo.Baggins"))
                .andExpect(jsonPath("$.password").value("password12"));
    }

    @Test
    public void shouldReturn400WhenFirstNameBlank() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("");
        request.setLastName("Baggins");
        request.setSpecialization(TrainingTypeName.FITNESS.toString());

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnProfile() throws Exception {
        Trainer trainer = generateTrainer();
        when(trainerService.findByUsername("Bilbo.Baggins")).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Bilbo.Baggins"))
                .andExpect(jsonPath("$.firstName").value("Bilbo"));
    }

    @Test
    public void shouldReturn403WhenAccessingAnotherUser() throws Exception {
        mockMvc.perform(get("/api/trainers/Saruman.White")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturn404WhenNotFound() throws Exception {
        when(trainerService.findByUsername("Bilbo.Baggins"))
                .thenThrow(new UserNotFoundException("Bilbo.Baggins"));

        mockMvc.perform(get("/api/trainers/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUpdatedProfile() throws Exception {
        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest();
        request.setFirstName("Bilbo");
        request.setLastName("Baggins");
        request.setIsActive(true);

        Trainer existing = generateTrainer();
        when(trainerService.findByUsername("Bilbo.Baggins")).thenReturn(existing);
        when(trainerService.update(any(Trainer.class))).thenReturn(existing);

        mockMvc.perform(put("/api/trainers/Bilbo.Baggins")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bilbo"));
    }

    @Test
    public void shouldReturnTrainings() throws Exception {
        Trainer trainer = generateTrainer();
        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(new Trainee());
        training.getTrainee().setUsername("Gandalf.Grey");
        training.setTrainingType(generateSpecialization());
        training.setTrainingName("Yoga");
        training.setTrainingDate(LocalDate.of(2026, 10, 15));
        training.setTrainingDuration(Duration.ofHours(1));

        when(trainingService.getTrainerTrainings(eq("Bilbo.Baggins"), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainers/Bilbo.Baggins/trainings")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"));
    }

    @Test
    public void shouldReturn200WhenSetActive() throws Exception {
        ActivationRequest request = new ActivationRequest();
        request.setIsActive(false);

        mockMvc.perform(patch("/api/trainers/Bilbo.Baggins/activate")
                        .requestAttr(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private Trainer generateTrainer() {
        Trainer t = new Trainer();
        t.setUsername("Bilbo.Baggins");
        t.setFirstName("Bilbo");
        t.setLastName("Baggins");
        t.setActive(true);
        t.setSpecialization(generateSpecialization());
        return t;
    }

    private TrainingType generateSpecialization() {
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.FITNESS);
        return type;
    }
}
