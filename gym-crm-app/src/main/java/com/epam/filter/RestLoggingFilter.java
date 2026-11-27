package com.epam.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RestLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(request, response);
        } finally {
            logRequest(request);
            logResponse(response);
        }
    }

    private void logRequest(HttpServletRequest request) {
        Object matchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String endpoint = matchingPattern instanceof String pattern ? pattern : "unmatched";
        LOG.info("REST request: {} {}", request.getMethod(), endpoint);
    }

    private void logResponse(HttpServletResponse response) {
        int status = response.getStatus();
        LOG.info("REST response: status={} outcome={}", status, status < 400 ? "success" : "error");
    }
}
