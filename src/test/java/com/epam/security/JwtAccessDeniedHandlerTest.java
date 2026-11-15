package com.epam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtAccessDeniedHandlerTest {

    @Test
    public void shouldReturnForbiddenJsonResponse() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/trainees");
        var response = new MockHttpServletResponse();
        var handler = new JwtAccessDeniedHandler(new ObjectMapper());

        handler.handle(request, response, new AccessDeniedException("Forbidden"));

        assertEquals(403, response.getStatus());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(response.getHeader(HttpHeaders.CONTENT_TYPE))));
        assertEquals("Access denied",
                new ObjectMapper().readTree(response.getContentAsString()).get("message").asText());
    }
}
