package com.epam.workload.service;

import com.epam.workload.domain.WorkloadChange;
import com.epam.workload.domain.MonthlySummary;

public interface TrainerWorkloadService {

    void updateWorkload(WorkloadChange change);

    MonthlySummary getMonthlyWorkload(String username, int year, int month);
}
