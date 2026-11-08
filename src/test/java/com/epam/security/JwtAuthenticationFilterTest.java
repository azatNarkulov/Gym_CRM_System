package com.epam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldPassThroughWhenNoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void shouldPassThroughWhenHeaderHasWrongPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xxx");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad");
        when(jwtService.isValid("bad")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void shouldAuthenticateValidToken() throws Exception {
        UserDetails details = User.withUsername("Bilbo.Baggins")
                .password("x")
                .authorities("ROLE_TRAINEE")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.isValid("good")).thenReturn(true);
        when(jwtService.extractUsername("good")).thenReturn("Bilbo.Baggins");
        when(userDetailsService.loadUserByUsername("Bilbo.Baggins")).thenReturn(details);

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("Bilbo.Baggins", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(request).setAttribute(SecurityConstants.AUTHENTICATED_USERNAME_ATTR, "Bilbo.Baggins");
        verify(chain).doFilter(request, response);
    }

    @Test
    public void shouldNotAuthenticateDisabledUser() throws Exception {
        UserDetails details = User.withUsername("Bilbo.Baggins")
                .password("x")
                .authorities("ROLE_TRAINEE")
                .disabled(true)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.isValid("good")).thenReturn(true);
        when(jwtService.extractUsername("good")).thenReturn("Bilbo.Baggins");
        when(userDetailsService.loadUserByUsername("Bilbo.Baggins")).thenReturn(details);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void shouldNotAuthenticateBlackListedToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.isValid("good")).thenReturn(true);
        when(jwtService.extractJti("good")).thenReturn("jti123");
        when(tokenBlacklistService.isBlacklisted("jti123")).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
