package com.epam.security;

import com.epam.exception.TooManyRequestsException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginAttemptService.class);
    private static final int MAX_ATTEMPTS = 3;
//    private static final long BLOCK_DURATION_MILLIS = 20 * 1000L; // 20 sec
    private static final long BLOCK_DURATION_MILLIS = 5 * 60 * 1000L; // 5 min
    private static final long FIXED_RATE = 10 * 60 * 1000L; // 10 min
    private static final long CLEANUP_THRESHOLD_MILLIS = 60 * 60 * 1000L; // 1 h

    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attemptsCache.getOrDefault(username, new LoginAttempt());
        attempt.increment();
        if (attempt.getCount() >= MAX_ATTEMPTS) {
            attempt.setBlockedUntil(Instant.now().plusMillis(BLOCK_DURATION_MILLIS));
            LOG.warn("User {} blocked for 5 minutes due to many failed login attempts", username);
        }
        attemptsCache.put(username, attempt);
    }

    public void checkBlocked(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt != null && attempt.getBlockedUntil() != null) {
            if (Instant.now().isBefore(attempt.getBlockedUntil())) {
                throw new TooManyRequestsException("Account is temporarily blocked. Try again later");
            } else {
                attemptsCache.remove(username);
            }
        }
    }

    @Scheduled(fixedRate = FIXED_RATE)
    public void cleanup() {
        Instant threshold = Instant.now().minusMillis(CLEANUP_THRESHOLD_MILLIS);
        int initialSize = attemptsCache.size();

        attemptsCache.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            return attempt.getLastAttempt() != null
                    && attempt.getLastAttempt().isBefore(threshold)
                    && (attempt.getBlockedUntil() == null || attempt.getBlockedUntil().isBefore(Instant.now()));
        });

        int removed = initialSize - attemptsCache.size();
        if (removed > 0) {
            LOG.debug("Cleaned up {} login attempts", removed);
        }
    }

    @Getter
    private static class LoginAttempt {
        private int count = 0;
        @Setter
        private Instant blockedUntil;
        private Instant lastAttempt;

        public void increment() {
            count++;
            lastAttempt = Instant.now();
        }

    }
}
