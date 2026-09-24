package com.epam.domain;

import lombok.Data;

@Data
public class User extends BaseEntity {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
}
