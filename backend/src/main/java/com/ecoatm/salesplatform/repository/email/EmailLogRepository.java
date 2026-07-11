package com.ecoatm.salesplatform.repository.email;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    /**
     * Rows in {@code status} that are due for another send attempt: their
     * {@code next_attempt_at} has already elapsed and {@code retry_count}
     * is still below the cap. Backs T6's {@code EmailRetryWorker}.
     */
    List<EmailLog> findByStatusAndNextAttemptAtLessThanEqualAndRetryCountLessThan(
            EmailStatus status, Instant cutoff, int retryCountCap);

    /**
     * Paged listing by status. Backs T9's admin log list; the full
     * multi-field filter query is built there.
     */
    Page<EmailLog> findByStatus(EmailStatus status, Pageable pageable);
}
