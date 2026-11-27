package com.epam.workload.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WorkloadTransactionIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Transaction-Id";
    public static final String MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String transactionId = getOrCreateTransactionId(request.getHeader(HEADER));
        MDC.put(MDC_KEY, transactionId);
        request.setAttribute(MDC_KEY, transactionId);
        response.setHeader(HEADER, transactionId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private String getOrCreateTransactionId(String incomingId) {
        if (incomingId != null) {
            try {
                return UUID.fromString(incomingId).toString();
            } catch (IllegalArgumentException ignored) {
                // Generate a new ID rather than putting untrusted header content into logs.
            }
        }
        return UUID.randomUUID().toString();
    }
}
