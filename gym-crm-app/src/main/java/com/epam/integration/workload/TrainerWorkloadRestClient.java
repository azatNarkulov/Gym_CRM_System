package com.epam.integration.workload;

import com.epam.dto.request.WorkloadUpdateRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TrainerWorkloadRestClient implements TrainerWorkloadClient {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadRestClient.class);
    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public TrainerWorkloadRestClient(RestClient restClient, CircuitBreaker trainerWorkloadCircuitBreaker) {
        this.restClient = restClient;
        this.circuitBreaker = trainerWorkloadCircuitBreaker;
    }

    @Override
    public void updateWorkload(WorkloadUpdateRequest request) {
        try {
            circuitBreaker.executeRunnable(() -> restClient.post()
                    .uri("/api/workload")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity());
        } catch (CallNotPermittedException exception) {
            LOG.warn("Trainer Workload circuit breaker is open");
            throw new TrainerWorkloadUnavailableException(exception);
        } catch (RestClientException exception) {
            LOG.warn("Trainer Workload request failed: {}", exception.getMessage());
            throw new TrainerWorkloadUnavailableException(exception);
        } catch (IllegalStateException exception) {
            LOG.warn("Trainer Workload discovery or request failed", exception);
            throw new TrainerWorkloadUnavailableException(exception);
        }
    }
}
