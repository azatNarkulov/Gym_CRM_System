package com.epam.workload.domain;

import java.time.Duration;
import java.time.YearMonth;

public record MonthlySummary(
        String username, String firstName, String lastName, boolean active,
        YearMonth month, Duration duration) {
}
