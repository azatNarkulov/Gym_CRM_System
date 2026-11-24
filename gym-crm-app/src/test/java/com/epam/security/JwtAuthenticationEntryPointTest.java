package com.epam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtAuthenticationEntryPointTest {

    @Test
    public void shouldReturnUnauthorizedJsonResponse() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/trainees");
        var response = new MockHttpServletResponse();
        var entryPoint = new JwtAuthenticationEntryPoint(new ObjectMapper());

        entryPoint.commence(request, response, new BadCredentialsException("Unauthorized"));

        assertEquals(401, response.getStatus());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(response.getHeader(HttpHeaders.CONTENT_TYPE))));
        assertEquals("Authentication required",
                new ObjectMapper().readTree(response.getContentAsString()).get("message").asText());
    }
}
