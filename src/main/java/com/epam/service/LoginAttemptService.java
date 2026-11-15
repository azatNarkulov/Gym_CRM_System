package com.epam.service;

import com.epam.exception.TooManyRequestsException;
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
    private static final long BLOCK_DURATION_MILLIS = 5 * 60 * 1000L; // 5 min
    private static final long FIXED_RATE = 10 * 60 * 1000L; // 10 min
    private static final long CLEANUP_THRESHOLD_MILLIS = 60 * 60 * 1000L; // 1 h

    private record LoginAttempt(int count, Instant blockedUntil, Instant lastAttempt) {}

    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public void loginFailed(String username) {
        Instant now = Instant.now();

       attemptsCache.compute(username, (key, previous) -> {
           LoginAttempt attempt = previous;

           if (attempt == null || (attempt.blockedUntil() != null && !now.isBefore(attempt.blockedUntil()))) {
               attempt = new LoginAttempt(0, null, now);
           }

           int count = attempt.count() + 1;
           Instant blockedUntil = attempt.blockedUntil();

           if (count >= MAX_ATTEMPTS && blockedUntil == null) {
               blockedUntil = now.plusMillis(BLOCK_DURATION_MILLIS);
               LOG.warn("User {} blocked for 5 minutes due to many failed login attempts", username);
           }

           return new LoginAttempt(count, blockedUntil, now);
       });
    }

    public void checkBlocked(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null || attempt.blockedUntil() == null) {
            return;
        }

        if (Instant.now().isBefore(attempt.blockedUntil())) {
            throw new TooManyRequestsException();
        }

        attemptsCache.remove(username, attempt);
    }

    @Scheduled(fixedRate = FIXED_RATE)
    public void cleanup() {
        Instant threshold = Instant.now().minusMillis(CLEANUP_THRESHOLD_MILLIS);
        int initialSize = attemptsCache.size();

        attemptsCache.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            return attempt.lastAttempt() != null
                    && attempt.lastAttempt().isBefore(threshold)
                    && (attempt.blockedUntil() == null || attempt.blockedUntil().isBefore(Instant.now()));
        });

        int removed = initialSize - attemptsCache.size();
        if (removed > 0) {
            LOG.debug("Cleaned up {} login attempts", removed);
        }
    }
}
