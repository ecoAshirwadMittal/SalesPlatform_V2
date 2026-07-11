package com.ecoatm.salesplatform.service.email;

import com.ecoatm.salesplatform.dto.email.SmtpConfigUpdate;
import com.ecoatm.salesplatform.model.email.SmtpConfig;
import com.ecoatm.salesplatform.repository.email.SmtpConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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

    /**
     * Applies {@code patch} to the singleton {@code email.smtp_config} row,
     * stamps {@code changed_date}/{@code changed_by_id}, persists, and
     * invalidates the cache so the next {@link #get()} reloads the fresh
     * row instead of serving the pre-update snapshot.
     *
     * <p>Fields left {@code null} on {@code patch} are left unchanged —
     * mirrors {@code EmailTemplateServiceImpl.update}'s null-means-unchanged
     * convention. The admin SMTP settings form only needs to submit the
     * fields the admin actually edited.
     *
     * <p>{@code changedByUserId} MUST be resolved by the caller from the
     * authenticated principal (see {@code AdminEmailController}) — this
     * method trusts whatever id it is given, so it must never be sourced
     * from a request parameter or body field.
     *
     * <p>{@link SmtpConfigUpdate} has no password component (design
     * decision D2 — SMTP credentials are env-only,
     * {@code spring.mail.password}), so there is no code path here that
     * could ever persist one.
     *
     * @throws IllegalStateException if the id=1 singleton row is missing
     *     (can't-happen post-V92 seed — mirrors {@link #get()}).
     */
    @Transactional
    public SmtpConfig update(SmtpConfigUpdate patch, Long changedByUserId) {
        SmtpConfig row = repository.findById(SINGLETON_ID)
                .orElseThrow(() -> new IllegalStateException(
                        "email.smtp_config singleton row (id=1) missing"));

        if (patch.serverHost() != null) {
            row.setServerHost(patch.serverHost());
        }
        if (patch.serverPort() != null) {
            row.setServerPort(patch.serverPort());
        }
        if (patch.protocol() != null) {
            row.setProtocol(patch.protocol());
        }
        if (patch.fromAddress() != null) {
            row.setFromAddress(patch.fromAddress());
        }
        if (patch.fromDisplayName() != null) {
            row.setFromDisplayName(patch.fromDisplayName());
        }
        if (patch.replyTo() != null) {
            row.setReplyTo(patch.replyTo());
        }
        if (patch.useSsl() != null) {
            row.setUseSsl(patch.useSsl());
        }
        if (patch.useTls() != null) {
            row.setUseTls(patch.useTls());
        }
        if (patch.enabled() != null) {
            row.setEnabled(patch.enabled());
        }
        if (patch.maxRetryAttempts() != null) {
            row.setMaxRetryAttempts(patch.maxRetryAttempts());
        }
        if (patch.timeoutMs() != null) {
            row.setTimeoutMs(patch.timeoutMs());
        }

        row.setChangedDate(Instant.now());
        row.setChangedById(changedByUserId);

        SmtpConfig saved = repository.save(row);
        invalidate();
        return saved;
    }
}
