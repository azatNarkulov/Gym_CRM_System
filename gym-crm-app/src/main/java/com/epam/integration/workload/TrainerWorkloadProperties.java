package com.epam.integration.workload;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "trainer.workload")
public record TrainerWorkloadProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout) {
}
