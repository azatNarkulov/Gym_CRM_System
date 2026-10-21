package com.epam.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    public static final String MDC_KEY = "transactionId";
    public static final String REQUEST_ATTR = "transactionId";
    public static final String HEADER = "X-Transaction-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, transactionId);
        req.setAttribute(REQUEST_ATTR, transactionId);
        if (res instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader(HEADER, transactionId);
        }
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
