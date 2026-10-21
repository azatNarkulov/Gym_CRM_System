package com.epam.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHealthIndicator.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return Health.up().withDetail("message", "Database is up").build();
            } else {
                LOG.warn("Database health check returned an unexpected result");
                return Health.down().withDetail("message", "Database is not up").build();
            }
        } catch (Exception e) {
            LOG.error("Database health check failed", e);
            return Health.down(e).build();
        }
    }
}
