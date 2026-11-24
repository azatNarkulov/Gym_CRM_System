package com.epam.workload.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record WorkloadSummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean trainerStatus,
        List<YearSummary> years) {

    public record YearSummary(int year, List<MonthSummary> months) {
    }

    public record MonthSummary(int month, BigDecimal trainingSummaryDurationHours) {
    }
}
