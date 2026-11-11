package com.epam.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private static final Logger LOG = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final long CLEANUP_RATE = 5 * 60 * 1000L; // 5 min

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, Instant expiration) {
        if (jti != null && expiration != null) {
            blacklist.put(jti, expiration);
            LOG.debug("Token {} added to blacklist", jti);
        }
    }

    public boolean isBlacklisted(String jti) {
        Instant exp = blacklist.get(jti);
        if (exp == null) {
            return false;
        }
        if (Instant.now().isAfter(exp)) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = CLEANUP_RATE)
    public void cleanup() {
        Instant now = Instant.now();
        int initialSize = blacklist.size();

        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));

        int removed = initialSize - blacklist.size();
        if (removed > 0) {
            LOG.debug("Cleaned up {} expired blacklisted tokens", removed);
        }
    }
}
