package com.ecoatm.salesplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Hourly cleanup of expired sessions from identity.sessions.
 * Finding 20 — database remediation Phase 5.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupTask {

    private final JdbcTemplate jdbc;

    @Scheduled(fixedRate = 3_600_000) // hourly
    public void cleanupExpiredSessions() {
        int deleted = jdbc.update(
                "DELETE FROM identity.sessions WHERE last_active < NOW() - INTERVAL '30 days'");
        if (deleted > 0) {
            log.info("Session cleanup: removed {} expired session(s)", deleted);
        }
    }
}
