package com.epam.integration.workload;

import com.epam.dto.request.WorkloadAction;
import com.epam.dto.request.WorkloadUpdateRequest;
import com.epam.filter.TransactionIdFilter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

public class TrainerWorkloadRestClientTest {

    @AfterEach
    public void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldForwardBearerTokenAndTransactionId() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder
                .baseUrl("http://workload")
                .requestInterceptor(new TrainerWorkloadRequestInterceptor())
                .build();
        TrainerWorkloadRestClient client =
                new TrainerWorkloadRestClient(restClient, CircuitBreaker.ofDefaults("trainerWorkloadTest"));
        MockHttpServletRequest incomingRequest = new MockHttpServletRequest();
        incomingRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer jwt-token");
        incomingRequest.setAttribute(TransactionIdFilter.REQUEST_ATTR, "transaction-123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(incomingRequest));
        server.expect(requestTo("http://workload/api/workload"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-token"))
                .andExpect(header(TransactionIdFilter.HEADER, "transaction-123"))
                .andExpect(jsonPath("$.actionType").value("ADD"))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON));

        client.updateWorkload(request());

        server.verify();
    }

    @Test
    public void shouldMapRemoteErrorsToUnavailableAndOpenCircuit() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.baseUrl("http://workload").build();
        CircuitBreaker breaker = CircuitBreaker.of(
                "trainerWorkloadTest",
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .build());
        TrainerWorkloadRestClient client = new TrainerWorkloadRestClient(restClient, breaker);
        server.expect(requestTo("http://workload/api/workload"))
                .andRespond(withServerError());
        server.expect(requestTo("http://workload/api/workload"))
                .andRespond(withServerError());

        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));
        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));
        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));

        server.verify();
    }

    @Test
    public void shouldMapMissingDiscoveryInstancesToUnavailableAndOpenCircuit() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://trainer-workload")
                .requestInterceptor((request, body, execution) -> {
                    throw new IllegalStateException("No instances available for trainer-workload");
                })
                .build();
        CircuitBreaker breaker = CircuitBreaker.of(
                "trainerWorkloadDiscoveryTest",
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .build());
        TrainerWorkloadRestClient client = new TrainerWorkloadRestClient(restClient, breaker);

        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));
        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));
        assertEquals(CircuitBreaker.State.OPEN, breaker.getState());
        assertThrows(TrainerWorkloadUnavailableException.class, () -> client.updateWorkload(request()));
    }

    private WorkloadUpdateRequest request() {
        return new WorkloadUpdateRequest(
                "trainer.username",
                "First",
                "Last",
                true,
                LocalDate.of(2026, 11, 20),
                Duration.ofHours(1),
                WorkloadAction.ADD);
    }
}
