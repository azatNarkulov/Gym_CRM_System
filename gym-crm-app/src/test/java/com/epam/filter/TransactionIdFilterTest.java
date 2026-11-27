package com.epam.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransactionIdFilterTest {

    @Mock private ServletRequest request;
    @Mock private HttpServletResponse response;

    private final TransactionIdFilter filter = new TransactionIdFilter();

    @Test
    void shouldPutTransactionIdInMdcAndCleanUpAfter() throws Exception {
        String[] captured = new String[1];
        FilterChain chain = (req, res) -> captured[0] = MDC.get(TransactionIdFilter.MDC_KEY);

        filter.doFilter(request, response, chain);

        assertNotNull(captured[0]);
        verify(request).setAttribute(eq(TransactionIdFilter.REQUEST_ATTR), anyString());
        verify(response).setHeader(eq(TransactionIdFilter.HEADER), anyString());
        assertNull(MDC.get(TransactionIdFilter.MDC_KEY));
    }

    @Test
    void shouldCleanMdcEvenIfChainThrows() {
        FilterChain chain = (req, res) -> { throw new RuntimeException("boom"); };

        try {
            filter.doFilter(request, response, chain);
        } catch (Exception ignored) {
        }

        assertNull(MDC.get(TransactionIdFilter.MDC_KEY));
    }
}
