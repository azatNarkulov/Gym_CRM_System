package com.epam.object;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
    private Long userId;
}
