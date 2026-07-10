package com.ecoatm.salesplatform.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * H-10 (security review 2026-07-10): the upload rate limiter must cap multipart
 * requests per client IP per minute so a single host cannot flood the
 * DOM-loading upload parsers. Mirrors {@link AuthRateLimiterTest}.
 */
class UploadRateLimiterTest {

    private static Clock fixedClock() {
        return Clock.fixed(Instant.ofEpochSecond(1_000_000), ZoneOffset.UTC);
    }

    @Test
    void allowsUpToCapThenBlocksSameKey() {
        UploadRateLimiter limiter = new UploadRateLimiter(fixedClock(), 3);
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isTrue();
        assertThat(limiter.tryAcquire("1.2.3.4")).isFalse(); // 4th exceeds cap of 3
    }

    @Test
    void keysAreIndependent() {
        UploadRateLimiter limiter = new UploadRateLimiter(fixedClock(), 1);
        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();
        // a different caller has its own bucket
        assertThat(limiter.tryAcquire("2.2.2.2")).isTrue();
    }

    @Test
    void nullOrBlankKeyFoldsIntoOneBucket() {
        UploadRateLimiter limiter = new UploadRateLimiter(fixedClock(), 1);
        assertThat(limiter.tryAcquire(null)).isTrue();
        assertThat(limiter.tryAcquire("")).isFalse(); // same "unknown" bucket, now over cap
    }

    @Test
    void clientIp_prefersFirstXForwardedForHop() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.7, 10.0.0.1, 10.0.0.2");
        request.setRemoteAddr("192.168.1.5");
        assertThat(UploadRateLimiter.clientIp(request)).isEqualTo("203.0.113.7");
    }

    @Test
    void clientIp_fallsBackToRemoteAddrWhenNoHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.5");
        assertThat(UploadRateLimiter.clientIp(request)).isEqualTo("192.168.1.5");
    }

    @Test
    void clientIp_nullRequestIsUnknown() {
        assertThat(UploadRateLimiter.clientIp(null)).isEqualTo("unknown");
    }
}
