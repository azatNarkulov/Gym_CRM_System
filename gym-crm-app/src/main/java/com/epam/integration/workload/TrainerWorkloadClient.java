package com.epam.integration.workload;

import com.epam.dto.request.WorkloadUpdateRequest;

public interface TrainerWorkloadClient {

    void updateWorkload(WorkloadUpdateRequest request);
}
