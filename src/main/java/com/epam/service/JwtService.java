package com.epam.service;

import com.epam.config.JwtProperties;
import com.epam.domain.Trainer;
import com.epam.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final Logger LOG = LoggerFactory.getLogger(JwtService.class);
    private static final String CLAIM_ROLE = "role";
    private static final String ROLE_TRAINEE = "TRAINEE";
    private static final String ROLE_TRAINER = "TRAINER";
    private static final int MIN_KEY_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtService(JwtProperties properties) {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(properties.secret());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("security.jwt.secret must be valid base64", e);
        }
        if (keyBytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("security.jwt.secret must decode to at least " + MIN_KEY_BYTES + " bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = properties.expiration().toMillis();
        LOG.info("JwtService initialized (expiration={} ms)", expirationMillis);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim(CLAIM_ROLE, resolveRole(user))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(signingKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            LOG.debug("Invalid JWT", e);
            return false;
        }
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public String extractJti(String token) {
        return parse(token).getId();
    }

    private String resolveRole(User user) {
        return user instanceof Trainer ? ROLE_TRAINER : ROLE_TRAINEE;
    }
}
