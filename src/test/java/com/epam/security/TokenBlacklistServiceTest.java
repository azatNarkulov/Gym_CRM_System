package com.epam.security;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenBlacklistServiceTest {

    private final TokenBlacklistService service = new TokenBlacklistService();

    @Test
    public void shouldBlacklistTokenUntilExpiration() {
        service.blacklist("token-1", Instant.now().plusSeconds(60));

        assertTrue(service.isBlacklisted("token-1"));
    }

    @Test
    public void shouldIgnoreNullTokenIdOrExpiration() {
        service.blacklist(null, Instant.now().plusSeconds(60));
        service.blacklist("token-1", null);

        assertFalse(service.isBlacklisted("token-1"));
    }

    @Test
    public void shouldRemoveExpiredTokenWhenChecked() {
        service.blacklist("expired-token", Instant.now().minusSeconds(1));

        assertFalse(service.isBlacklisted("expired-token"));
        assertFalse(service.isBlacklisted("expired-token"));
    }

    @Test
    public void shouldRemoveExpiredTokens() {
        service.blacklist("expired-token", Instant.now().minusSeconds(1));
        service.blacklist("active-token", Instant.now().plusSeconds(60));

        service.cleanup();

        assertFalse(service.isBlacklisted("expired-token"));
        assertTrue(service.isBlacklisted("active-token"));
    }
}
