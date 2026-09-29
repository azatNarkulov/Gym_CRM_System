package com.epam.service;

import com.epam.domain.User;

public interface UserService<T extends User> extends AbstractService<T> {

    T update(T user);
}
