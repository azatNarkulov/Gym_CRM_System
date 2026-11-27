package com.epam.integration.workload;

import com.epam.dto.request.WorkloadUpdateRequest;
import com.epam.filter.TransactionIdFilter;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;

@Component
public class TrainerWorkloadRestClient implements TrainerWorkloadClient {
    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadRestClient.class);
    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final TrainerWorkloadProperties properties;
    private final Function<URI, URI> addressResolver;

    public TrainerWorkloadRestClient(RestClient restClient, CircuitBreaker trainerWorkloadCircuitBreaker,
            TrainerWorkloadProperties properties, Function<URI, URI> addressResolver) {
        this.restClient = restClient;
        this.circuitBreaker = trainerWorkloadCircuitBreaker;
        this.properties = properties;
        this.addressResolver = addressResolver;
    }

    @Override
    public void updateWorkload(WorkloadUpdateRequest request) {
        try {
            circuitBreaker.executeRunnable(() -> {
                URI logicalAddress = UriComponentsBuilder.fromUriString(properties.baseUrl())
                        .path("/api/trainers/{username}/workload")
                        .buildAndExpand(request.trainerUsername()).encode().toUri();
                URI address = addressResolver.apply(logicalAddress);
                restClient.post().uri(address).headers(headers -> copyRequestHeaders(headers))
                        .body(request).retrieve().toBodilessEntity();
            });
        } catch (CallNotPermittedException | RestClientException | TrainerWorkloadUnavailableException exception) {
            LOG.error("Unable to update Trainer Workload; the main operation will continue", exception);
        }
    }

    private void copyRequestHeaders(HttpHeaders headers) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest incoming = attributes.getRequest();
            String authorization = incoming.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION, authorization);
            }
            Object transactionId = incoming.getAttribute(TransactionIdFilter.REQUEST_ATTR);
            if (transactionId instanceof String id && !id.isBlank()) {
                headers.set(TransactionIdFilter.HEADER, id);
            }
        }
    }
}
