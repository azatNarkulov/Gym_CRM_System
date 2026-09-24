package com.epam.service;

import com.epam.domain.User;
import com.epam.repository.AbstractUserDao;
import com.epam.util.PasswordGenerator;
import com.epam.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class AbstractUserService<T extends User> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUserService.class);

    protected AbstractUserDao<T> userDao;
    protected UsernameGenerator usernameGenerator;
    protected PasswordGenerator passwordGenerator;

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public T add(T user) {
        LOG.info("Creating user: {} {}", user.getFirstName(), user.getLastName());

        user.setUsername(usernameGenerator.generate(user));
        user.setPassword(passwordGenerator.generate());

        return userDao.add(user);
    }

    public T update(T user) {
        LOG.info("Updating user: id={}", user.getId());

        return userDao.update(user);
    }

    public Optional<T> get(Long id) {
        LOG.debug("Getting user: id={}", id);

        return Optional.ofNullable(userDao.get(id));
    }
}
