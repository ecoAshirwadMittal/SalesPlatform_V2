package com.ecoatm.salesplatform.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window per-client-IP rate limiter for multipart upload endpoints
 * (security review 2026-07-10, H-10).
 *
 * <p>Every multipart POST except the bidder bid-import (which has its own
 * per-(user, round) {@code BidRateLimiter}) was previously unthrottled, so a
 * single host could pair a large file with the DOM-loading POI parsers (H-9)
 * for a cheap denial-of-service. This caps requests per IP per minute.
 *
 * <p>Mirrors {@link AuthRateLimiter} exactly — a {@link ConcurrentHashMap} of
 * minute-bucketed counters with a scheduled eviction sweep so the map stays
 * bounded. Keyed by IP (not user id) because several guarded endpoints resolve
 * their principal late and because upload flooding is a per-origin concern.
 */
@Component
public class UploadRateLimiter {

    private final Clock clock;
    private final int maxPerMinute;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public UploadRateLimiter(Clock clock,
                             @Value("${upload.rate-limit.max-per-minute:20}") int maxPerMinute) {
        this.clock = clock;
        this.maxPerMinute = maxPerMinute;
    }

    /** @return {@code true} if allowed, {@code false} once the per-minute cap is exceeded. */
    public boolean tryAcquire(String clientIp) {
        String key = (clientIp == null || clientIp.isBlank()) ? "unknown" : clientIp;
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

    /**
     * Resolves the caller's IP for rate-limit bucketing — prefers the first hop
     * in {@code X-Forwarded-For} (behind a proxy/CDN) and falls back to the
     * socket address. Mirrors {@code AuthController.clientKey} so the login
     * limiter and the upload limiter bucket the same caller identically.
     */
    public static String clientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String remote = request.getRemoteAddr();
        return (remote == null || remote.isBlank()) ? "unknown" : remote;
    }

    @Scheduled(fixedDelayString = "${upload.rate-limit.cleanup-ms:300000}")
    void evictStaleBuckets() {
        long currentMinute = clock.instant().getEpochSecond() / 60;
        buckets.entrySet().removeIf(e -> e.getValue().minute() < currentMinute);
    }

    int bucketCount() {
        return buckets.size();
    }

    private record Bucket(long minute, AtomicInteger count) {}
}
