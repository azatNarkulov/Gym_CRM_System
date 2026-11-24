package com.epam.workload.service;

import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.dto.response.WorkloadSummaryResponse;

public interface TrainerWorkloadService {

    void updateWorkload(WorkloadUpdateRequest request);

    WorkloadSummaryResponse getMonthlyWorkload(String username, int year, int month);
}
