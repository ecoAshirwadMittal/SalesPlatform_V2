package com.ecoatm.salesplatform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window per-key rate limiter for the authentication endpoints (login,
 * forgot-password, reset-password).
 *
 * <p>Keyed by the caller's IP so a single host cannot brute-force credentials or
 * enumerate accounts at scale (security review 2026-07-10, H-6). Mirrors
 * {@code BidRateLimiter} — a stale-window eviction sweep keeps the map bounded.
 */
@Component
public class AuthRateLimiter {

    private final Clock clock;
    private final int maxPerMinute;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public AuthRateLimiter(Clock clock,
                           @Value("${auth.rate-limit.max-per-minute:10}") int maxPerMinute) {
        this.clock = clock;
        this.maxPerMinute = maxPerMinute;
    }

    /** @return {@code true} if allowed, {@code false} once the per-minute cap is exceeded. */
    public boolean tryAcquire(String key) {
        long minute = clock.instant().getEpochSecond() / 60;
        int[] countHolder = {0};
        buckets.compute(key, (k, existing) -> {
            if (existing == null || existing.minute != minute) {
                countHolder[0] = 1;
                return new Bucket(minute, new AtomicInteger(1));
            }
            countHolder[0] = existing.count.incrementAndGet();
            return existing;
        });
        return countHolder[0] <= maxPerMinute;
    }

    @Scheduled(fixedDelayString = "${auth.rate-limit.cleanup-ms:300000}")
    void evictStaleBuckets() {
        long currentMinute = clock.instant().getEpochSecond() / 60;
        buckets.entrySet().removeIf(e -> e.getValue().minute() < currentMinute);
    }

    int bucketCount() {
        return buckets.size();
    }

    private record Bucket(long minute, AtomicInteger count) {}
}
