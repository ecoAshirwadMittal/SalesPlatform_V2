package com.ecoatm.salesplatform.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * H-6 (security review 2026-07-10): the auth rate limiter must cap requests per
 * key per minute so a single host cannot brute-force credentials at scale.
 */
class AuthRateLimiterTest {

    private static Clock fixedClock() {
        return Clock.fixed(Instant.ofEpochSecond(1_000_000), ZoneOffset.UTC);
    }

    @Test
    void allowsUpToCapThenBlocksSameKey() {
        AuthRateLimiter limiter = new AuthRateLimiter(fixedClock(), 3);
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isFalse(); // 4th exceeds cap of 3
    }

    @Test
    void keysAreIndependent() {
        AuthRateLimiter limiter = new AuthRateLimiter(fixedClock(), 1);
        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();
        // a different caller has its own bucket
        assertThat(limiter.tryAcquire("2.2.2.2")).isTrue();
    }
}
