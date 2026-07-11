package com.ecoatm.salesplatform.service.email;

import com.ecoatm.salesplatform.model.email.SmtpConfig;
import com.ecoatm.salesplatform.repository.email.SmtpConfigRepository;
import org.springframework.stereotype.Service;

/**
 * Cached accessor for the singleton {@code email.smtp_config} row (id=1,
 * seeded by V92). Mirrors the "explicit-invalidation, no Spring Cache
 * infra" style {@code EmailTemplateServiceImpl} uses for its own tiny
 * cache — a single row doesn't warrant pulling in Spring Cache
 * infrastructure.
 *
 * <p>{@link #invalidate()} is called after the admin PUT updates the row
 * so the next {@link #get()} reloads instead of serving a stale snapshot.
 */
@Service
public class SmtpConfigService {

    private static final long SINGLETON_ID = 1L;

    private final SmtpConfigRepository repository;

    private volatile SmtpConfig cached;

    public SmtpConfigService(SmtpConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns the cached singleton row, loading it on first call (or after
     * {@link #invalidate()}). V92 seeds the id=1 row, so a missing row is a
     * can't-happen guard rather than an expected runtime condition.
     */
    public SmtpConfig get() {
        SmtpConfig snapshot = cached;
        if (snapshot != null) {
            return snapshot;
        }
        synchronized (this) {
            if (cached == null) {
                cached = repository.findById(SINGLETON_ID)
                        .orElseThrow(() -> new IllegalStateException(
                                "email.smtp_config singleton row (id=1) missing"));
            }
            return cached;
        }
    }

    /** Clears the cache so the next {@link #get()} reloads from the DB. */
    public void invalidate() {
        cached = null;
    }

    /** The from-address fallback used when a template/message doesn't
     *  specify its own. */
    public String resolvedFromAddress() {
        return get().getFromAddress();
    }
}
