package com.ecoatm.salesplatform.repository.email;

import com.ecoatm.salesplatform.model.email.SmtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Singleton access to {@code email.smtp_config}. The single row always
 * has {@code id = 1} (DB-enforced) — callers load it with
 * {@code findById(1L)}; no extra finder is needed.
 */
@Repository
public interface SmtpConfigRepository extends JpaRepository<SmtpConfig, Long> {
}
