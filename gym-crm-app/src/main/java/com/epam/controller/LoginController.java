package com.epam.controller;

import com.epam.domain.User;
import com.epam.dto.request.ChangePasswordRequest;
import com.epam.dto.request.LoginRequest;
import com.epam.dto.response.LoginResponse;
import com.epam.service.JwtService;
import com.epam.service.LoginAttemptService;
import com.epam.service.TokenBlacklistService;
import com.epam.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Authentication", description = "Login, logout and password change")
@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserService userService,
                           LoginAttemptService loginAttemptService,
                           TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Operation(summary = "Login and receive JWT bearer token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        loginAttemptService.checkBlocked(request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            loginAttemptService.loginSucceeded(request.getUsername());

            User user = userService.findByUsername(request.getUsername());
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(request.getUsername());
            throw e;
        }
    }

    @Operation(summary = "Change password for authenticated user")
    @PutMapping("/login")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Logout (blacklist the token)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 7 is a length of "Bearer "
            try {
                Claims claims = jwtService.parse(token);
                String jti = claims.getId();
                Instant expiration = claims.getExpiration().toInstant();

                tokenBlacklistService.blacklist(jti, expiration);
            } catch (Exception e) {

            }
        }
        return ResponseEntity.ok().build();
    }
}
