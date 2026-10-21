package com.epam.filter;

import com.epam.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_USERNAME_ATTR = "authenticatedUsername";
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Basic ";

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (isPublic(request)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HEADER);
        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            writeUnauthorized(response, request);
            return;
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(authHeader.substring(PREFIX.length())), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            writeUnauthorized(response, request);
            return;
        }

        int colon = decoded.indexOf(':');
        if (colon < 0) {
            writeUnauthorized(response, request);
            return;
        }

        String username = decoded.substring(0, colon);
        String password = decoded.substring(colon + 1);

        if (!userService.matchCredentials(username, password)) {
            writeUnauthorized(response, request);
            return;
        }

        request.setAttribute(AUTHENTICATED_USERNAME_ATTR, username);
        chain.doFilter(request, response);
    }

    private boolean isPublic(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) {
            return true;
        }

        return "POST".equals(method) && ("/api/trainees/register".equals(path) || "/api/trainers/register".equals(path));
    }

    private void writeUnauthorized(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Invalid username or password");
        body.put("path", request.getRequestURI());
        body.put("transactionId", MDC.get(TransactionIdFilter.MDC_KEY));

        objectMapper.writeValue(response.getWriter(), body);
    }
}
