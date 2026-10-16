package com.epam.controller;

import com.epam.dto.request.ChangeLoginRequest;
import com.epam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "Authentication and password change")
@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Login")
    @GetMapping
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password for authenticated user")
    @PutMapping
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangeLoginRequest request) {
        userService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
