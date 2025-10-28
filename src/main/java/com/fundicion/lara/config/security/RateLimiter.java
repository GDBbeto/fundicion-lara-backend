package com.fundicion.lara.config.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 60_000; // 1 minuto

    public boolean isAllowed(String key) {
        var attempt = attempts.get(key);
        if (attempt == null) return true;

        if (attempt.count >= MAX_ATTEMPTS) {
            if (Instant.now().isBefore(attempt.lockedUntil)) {
                return false;
            } else {
                attempts.remove(key);
                return true;
            }
        }

        return true;
    }

    public void recordFailedAttempt(String key) {
        var attempt = attempts.getOrDefault(key, new LoginAttempt());
        attempt.count++;
        if (attempt.count >= MAX_ATTEMPTS) {
            attempt.lockedUntil = Instant.now().plusMillis(LOCK_TIME);
        }
        attempts.put(key, attempt);
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private static class LoginAttempt {
        int count = 0;
        Instant lockedUntil = Instant.now();
    }
}
