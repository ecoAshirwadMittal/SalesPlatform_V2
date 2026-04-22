package com.ecoatm.salesplatform.service.auctions.biddata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BidRateLimiter {

    private final Clock clock;
    private final int maxPerMinute;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public BidRateLimiter(Clock clock,
                          @Value("${bidder.rate-limit.max-per-minute:60}") int maxPerMinute) {
        this.clock = clock;
        this.maxPerMinute = maxPerMinute;
    }

    public boolean tryAcquire(long userId, long bidRoundId) {
        String key = userId + ":" + bidRoundId;
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
     * Evicts buckets whose minute window has passed. Without this, the map
     * would grow unboundedly: every distinct {@code (user, bid_round)} pair
     * lives forever even after the bidder closes the page and the round
     * closes. Runs every 5 minutes; a bucket is stale once its minute is
     * strictly less than the current minute, because {@link #tryAcquire}
     * always overwrites a stale bucket on the next request.
     */
    @Scheduled(fixedDelayString = "${bidder.rate-limit.cleanup-ms:300000}")
    void evictStaleBuckets() {
        long currentMinute = clock.instant().getEpochSecond() / 60;
        buckets.entrySet().removeIf(e -> e.getValue().minute() < currentMinute);
    }

    int bucketCount() {
        return buckets.size();
    }

    private record Bucket(long minute, AtomicInteger count) {}
}
