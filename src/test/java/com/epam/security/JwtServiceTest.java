package com.epam.security;

import com.epam.config.JwtProperties;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtServiceTest {

    private static final String SECRET = "bXlTdXBlclNlY3JldEtleUZvckd5bUNybUFwcEF0TGVhc3QzMkJ5dGVz";
    private static final Duration EXPIRATION = Duration.ofMinutes(30);

    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService(new JwtProperties(SECRET, EXPIRATION));
    }

    @Test
    public void shouldGenerateTokenWithExpectedClaims() {
        Trainee trainee = generateTrainee();

        String token = jwtService.generateToken(trainee);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Claims claims = jwtService.parse(token);
        assertEquals("Bilbo.Baggins", claims.getSubject());
        assertEquals("TRAINEE", claims.get("role", String.class));
        assertNotNull(claims.getId());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    public void shouldMarkTrainerRoleForTrainer() {
        Trainer trainer = generateTrainer();

        String token = jwtService.generateToken(trainer);

        assertEquals("TRAINER", jwtService.extractRole(token));
    }

    @Test
    public void shouldProduceDistinctJtiForEachToken() {
        Trainee trainee = generateTrainee();

        String first = jwtService.generateToken(trainee);
        String second = jwtService.generateToken(trainee);

        assertNotEquals(jwtService.extractJti(first), jwtService.extractJti(second));
    }

    @Test
    public void shouldReturnTrueForValidToken() {
        Trainee trainee = generateTrainee();

        assertTrue(jwtService.isValid(jwtService.generateToken(trainee)));
    }

    @Test
    public void shouldReturnFalseForGarbageToken() {
        assertFalse(jwtService.isValid("not-a-jwt"));
        assertFalse(jwtService.isValid("a.b.c"));
    }

    @Test
    public void shouldRejectTokenSignedWithDifferentKey() {
        JwtProperties otherProps = new JwtProperties(
                Base64.getEncoder().encodeToString("a-different-32-bytes-secret-key!!!!!".getBytes()), EXPIRATION);
        JwtService otherService = new JwtService(otherProps);

        Trainee trainee = generateTrainee();
        String foreignToken = otherService.generateToken(trainee);

        assertFalse(jwtService.isValid(foreignToken));
    }

    @Test
    public void shouldFailForExpiredToken() throws InterruptedException {
        JwtService shortLived = new JwtService(new JwtProperties(SECRET, Duration.ofMillis(50)));

        Trainee trainee = generateTrainee();
        String token = shortLived.generateToken(trainee);

        Thread.sleep(100);

        assertFalse(shortLived.isValid(token));
        assertThrows(ExpiredJwtException.class, () -> shortLived.parse(token));
    }

    @Test
    public void shouldRejectSecretShorterThan32Bytes() {
        String tooShort = Base64.getEncoder().encodeToString("a".getBytes());

        assertThrows(IllegalStateException.class, () -> new JwtService(new JwtProperties(tooShort, EXPIRATION)));
    }

    @Test
    public void shouldRejectNonBase64Secret() {
        assertThrows(IllegalStateException.class, () -> new JwtService(new JwtProperties("not-base64", EXPIRATION)));
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Bilbo.Baggins");
        return trainee;
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Gandalf.Grey");
        return trainer;
    }
}
