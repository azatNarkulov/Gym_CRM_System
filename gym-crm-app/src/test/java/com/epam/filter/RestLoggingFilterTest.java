package com.epam.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestLoggingFilterTest {

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Test
    void shouldPassThroughAndCopyBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/trainees");
        request.setContent("{\"x\":1}".getBytes());

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            ((HttpServletResponse) res).setStatus(200);
            res.getWriter().write("ok");
        };

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    void shouldHandleQueryString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/trainees/Bilbo/trainings");
        request.setQueryString("fromDate=2026-01-01");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((HttpServletResponse) res).setStatus(200);

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
    }
}
