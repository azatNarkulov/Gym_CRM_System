package com.epam.controller;

import com.epam.dto.request.ChangeLoginRequest;
import com.epam.exception.AuthenticationException;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @InjectMocks
    private LoginController controller;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldReturn200WhenLogin() throws Exception {
        mockMvc.perform(get("/api/login"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn200WhenChangePassword() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Bilbo.Baggins");
        request.setOldPassword("password12");
        request.setNewPassword("password99");

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn401WhenOldPasswordWrong() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Bilbo.Baggins");
        request.setOldPassword("password12");
        request.setNewPassword("password99");

        doThrow(new AuthenticationException("wrong old password"))
                .when(userService).changePassword("Bilbo.Baggins", "password12", "password99");

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenBodyInvalid() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("");
        request.setOldPassword("");
        request.setNewPassword("");

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
