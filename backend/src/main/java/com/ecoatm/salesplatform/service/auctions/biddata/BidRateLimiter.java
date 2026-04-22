package com.ecoatm.salesplatform.service.auctions.biddata;

import org.springframework.beans.factory.annotation.Value;
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
        Bucket b = buckets.compute(key, (k, existing) -> {
            if (existing == null || existing.minute != minute) {
                return new Bucket(minute, new AtomicInteger(0));
            }
            return existing;
        });
        return b.count.incrementAndGet() <= maxPerMinute;
    }

    private record Bucket(long minute, AtomicInteger count) {}
}
