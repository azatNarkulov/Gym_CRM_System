package com.epam.service;

import com.epam.domain.User;

public interface UserCredentialsService {

    String generatePassword();
    String generateUsername(User user);
}
