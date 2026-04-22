package com.ecoatm.salesplatform.service.auctions.biddata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BidRateLimiterTest {

    private BidRateLimiter limiter;
    private Clock clock;
    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.parse("2026-04-23T10:00:00Z");
        clock = Clock.fixed(now, ZoneOffset.UTC);
        limiter = new BidRateLimiter(clock, /*maxPerMinute*/ 60);
    }

    @Test
    void allows_upToLimit() {
        for (int i = 0; i < 60; i++) {
            assertThat(limiter.tryAcquire(7L, 100L)).isTrue();
        }
        assertThat(limiter.tryAcquire(7L, 100L)).isFalse();
    }

    @Test
    void resetsAfterOneMinute() {
        for (int i = 0; i < 60; i++) limiter.tryAcquire(7L, 100L);
        assertThat(limiter.tryAcquire(7L, 100L)).isFalse();
        clock = Clock.fixed(now.plusSeconds(61), ZoneOffset.UTC);
        limiter = new BidRateLimiter(clock, 60);
        assertThat(limiter.tryAcquire(7L, 100L)).isTrue();
    }

    @Test
    void separateKeys_doNotShareBucket() {
        for (int i = 0; i < 60; i++) limiter.tryAcquire(7L, 100L);
        assertThat(limiter.tryAcquire(8L, 100L)).isTrue();
        assertThat(limiter.tryAcquire(7L, 200L)).isTrue();
    }

    @Test
    void evictStaleBuckets_removesPreviousMinuteEntries() {
        TestClock testClock = new TestClock(now);
        BidRateLimiter mutable = new BidRateLimiter(testClock, 60);

        mutable.tryAcquire(7L, 100L);
        mutable.tryAcquire(7L, 200L);
        assertThat(mutable.bucketCount()).isEqualTo(2);

        testClock.now = now.plusSeconds(120); // advance to minute+2
        mutable.tryAcquire(7L, 300L);         // creates a fresh bucket
        assertThat(mutable.bucketCount()).isEqualTo(3);

        mutable.evictStaleBuckets();
        assertThat(mutable.bucketCount()).isEqualTo(1);
    }

    private static final class TestClock extends Clock {
        volatile Instant now;
        TestClock(Instant initial) { this.now = initial; }
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }
}
