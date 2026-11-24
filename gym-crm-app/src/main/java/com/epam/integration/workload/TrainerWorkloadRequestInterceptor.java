package com.epam.integration.workload;

import com.epam.filter.TransactionIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Component
public class TrainerWorkloadRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            org.springframework.http.HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest incomingRequest = attributes.getRequest();
            copyHeader(incomingRequest, request.getHeaders(), HttpHeaders.AUTHORIZATION);

            Object transactionId = incomingRequest.getAttribute(TransactionIdFilter.REQUEST_ATTR);
            if (transactionId instanceof String id && !id.isBlank()) {
                request.getHeaders().set(TransactionIdFilter.HEADER, id);
            }
        }
        return execution.execute(request, body);
    }

    private void copyHeader(HttpServletRequest source, HttpHeaders target, String headerName) {
        String value = source.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            target.set(headerName, value);
        }
    }
}
