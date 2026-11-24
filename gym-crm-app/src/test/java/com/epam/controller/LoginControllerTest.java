package com.epam.controller;

import com.epam.domain.Trainee;
import com.epam.dto.request.ChangePasswordRequest;
import com.epam.dto.request.LoginRequest;
import com.epam.exception.GlobalExceptionHandler;
import com.epam.service.JwtService;
import com.epam.service.LoginAttemptService;
import com.epam.service.TokenBlacklistService;
import com.epam.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @InjectMocks
    private LoginController controller;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldReturnTokenOnValidLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("Bilbo.Baggins");
        request.setPassword("password12");

        Trainee trainee = new Trainee();
        trainee.setUsername("Bilbo.Baggins");
        trainee.setActive(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("Bilbo.Baggins", null, List.of()));
        when(userService.findByUsername("Bilbo.Baggins")).thenReturn(trainee);
        when(jwtService.generateToken(trainee)).thenReturn("jwt.token.value");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.value"));
    }

    @Test
    public void shouldReturn401WhenBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("Bilbo.Baggins");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenBlankUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldChangePasswordForAuthenticatedUser() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("password12");
        request.setNewPassword("password99");

        UserDetails principal = User.withUsername("Bilbo.Baggins")
                .password("x")
                .authorities("ROLE_TRAINEE")
                .build();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getAuthorities()));

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).changePassword("Bilbo.Baggins", "password12", "password99");
    }

    @Test
    public void shouldReturn200WhenLogout() throws Exception {
        String mockToken = "valid.jwt.token";

        Claims claims = Jwts.claims().id("some-jti").expiration(new Date(System.currentTimeMillis() + 60000)).build(); // 60000 = 1 min
        when(jwtService.parse(mockToken)).thenReturn(claims);

        mockMvc.perform(post("/api/logout").header("Authorization", "Bearer " + mockToken)).andExpect(status().isOk());

        verify(tokenBlacklistService).blacklist(eq("some-jti"), any(Instant.class));
    }
}
