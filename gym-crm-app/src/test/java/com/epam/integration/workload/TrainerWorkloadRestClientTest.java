package com.epam.integration.workload;

import com.epam.dto.request.WorkloadAction;
import com.epam.dto.request.WorkloadUpdateRequest;
import com.epam.filter.TransactionIdFilter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.http.HttpMethod.POST;

class TrainerWorkloadRestClientTest {
    private final TrainerWorkloadProperties properties =
            new TrainerWorkloadProperties("http://workload", Duration.ofSeconds(1), Duration.ofSeconds(2));

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void forwardsHeadersAndUsesTrainerResource() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        TrainerWorkloadRestClient client = new TrainerWorkloadRestClient(
                builder.build(), CircuitBreaker.ofDefaults("test"), properties, uri -> uri);
        MockHttpServletRequest incoming = new MockHttpServletRequest();
        incoming.addHeader(HttpHeaders.AUTHORIZATION, "Bearer test");
        incoming.setAttribute(TransactionIdFilter.REQUEST_ATTR, "transaction-123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(incoming));
        server.expect(requestTo("http://workload/api/trainers/trainer.username/workload"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test"))
                .andExpect(header(TransactionIdFilter.HEADER, "transaction-123"))
                .andExpect(jsonPath("$.actionType").value("ADD"))
                .andRespond(withSuccess());
        client.updateWorkload(request());
        server.verify();
    }

    @Test
    void remoteFailureIsLoggedAndDoesNotEscapeAndCircuitOpens() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CircuitBreaker breaker = breaker();
        TrainerWorkloadRestClient client = new TrainerWorkloadRestClient(builder.build(), breaker, properties, uri -> uri);
        server.expect(requestTo("http://workload/api/trainers/trainer.username/workload")).andRespond(withServerError());
        server.expect(requestTo("http://workload/api/trainers/trainer.username/workload")).andRespond(withServerError());
        assertDoesNotThrow(() -> client.updateWorkload(request()));
        assertDoesNotThrow(() -> client.updateWorkload(request()));
        assertEquals(CircuitBreaker.State.OPEN, breaker.getState());
        assertDoesNotThrow(() -> client.updateWorkload(request()));
        server.verify();
    }

    @Test
    void missingDiscoveryInstanceDoesNotEscape() {
        CircuitBreaker breaker = breaker();
        TrainerWorkloadRestClient client = new TrainerWorkloadRestClient(RestClient.create(), breaker, properties,
                uri -> { throw new TrainerWorkloadUnavailableException(new IllegalStateException("no instance")); });
        assertDoesNotThrow(() -> client.updateWorkload(request()));
        assertDoesNotThrow(() -> client.updateWorkload(request()));
        assertEquals(CircuitBreaker.State.OPEN, breaker.getState());
    }

    private CircuitBreaker breaker() {
        return CircuitBreaker.of("test", CircuitBreakerConfig.custom()
                .slidingWindowSize(2).minimumNumberOfCalls(2).failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30)).build());
    }

    private WorkloadUpdateRequest request() {
        return new WorkloadUpdateRequest("trainer.username", "First", "Last", true,
                LocalDate.of(2026, 11, 20), Duration.ofHours(1), WorkloadAction.ADD);
    }
}
