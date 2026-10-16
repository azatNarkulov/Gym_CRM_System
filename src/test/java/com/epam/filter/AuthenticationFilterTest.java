package com.epam.filter;

import com.epam.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private AuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationFilter(userService);
    }

    @Test
    void shouldReturn401WhenNoAuthHeader() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/trainees/Bilbo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldReturn401WhenWrongCredentials() throws Exception {
        String creds = Base64.getEncoder().encodeToString("user:wrong".getBytes(StandardCharsets.UTF_8));
        when(request.getRequestURI()).thenReturn("/api/trainees/Bilbo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Basic " + creds);
        when(userService.matchCredentials("user", "wrong")).thenReturn(false);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldPassThroughWhenCredentialsValid() throws Exception {
        String creds = Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
        when(request.getRequestURI()).thenReturn("/api/trainees/Bilbo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Basic " + creds);
        when(userService.matchCredentials("user", "pass")).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(request).setAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR, "user");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthForPublicEndpoint() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/trainees/register");
        when(request.getMethod()).thenReturn("POST");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturn401WhenMalformedBase64() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/trainees/Bilbo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Basic ***invalid***");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturn401WhenDecodedHasNoColon() throws Exception {
        String creds = Base64.getEncoder().encodeToString("nocolon".getBytes(StandardCharsets.UTF_8));
        when(request.getRequestURI()).thenReturn("/api/trainees/Bilbo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Basic " + creds);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
