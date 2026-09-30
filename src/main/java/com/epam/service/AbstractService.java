package com.epam.service;

import java.util.Optional;

public interface AbstractService<T> {

    T add(T entity);
    Optional<T> get(Long id);
}
