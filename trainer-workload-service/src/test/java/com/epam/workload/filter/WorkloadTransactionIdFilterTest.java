package com.epam.workload.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WorkloadTransactionIdFilterTest {

    private final WorkloadTransactionIdFilter filter = new WorkloadTransactionIdFilter();

    @Test
    public void shouldPropagateIncomingTransactionIdAndClearMdc() throws Exception {
        String transactionId = UUID.randomUUID().toString();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(WorkloadTransactionIdFilter.HEADER, transactionId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> transactionIdInChain = new AtomicReference<>();
        FilterChain chain = (servletRequest, servletResponse) ->
                transactionIdInChain.set(MDC.get(WorkloadTransactionIdFilter.MDC_KEY));

        filter.doFilter(request, response, chain);

        assertEquals(transactionId, transactionIdInChain.get());
        assertEquals(transactionId, response.getHeader(WorkloadTransactionIdFilter.HEADER));
        assertNull(MDC.get(WorkloadTransactionIdFilter.MDC_KEY));
    }

    @Test
    public void shouldGenerateTransactionIdWhenHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> transactionIdInChain = new AtomicReference<>();
        FilterChain chain = (servletRequest, servletResponse) ->
                transactionIdInChain.set(MDC.get(WorkloadTransactionIdFilter.MDC_KEY));

        filter.doFilter(request, response, chain);

        String transactionId = transactionIdInChain.get();
        assertEquals(UUID.fromString(transactionId).toString(), transactionId);
        assertEquals(transactionId, response.getHeader(WorkloadTransactionIdFilter.HEADER));
        assertNull(MDC.get(WorkloadTransactionIdFilter.MDC_KEY));
    }

    @Test
    public void shouldReplaceInvalidTransactionId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(WorkloadTransactionIdFilter.HEADER, "invalid-id");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        String responseTransactionId = response.getHeader(WorkloadTransactionIdFilter.HEADER);
        assertEquals(UUID.fromString(responseTransactionId).toString(), responseTransactionId);
    }
}
