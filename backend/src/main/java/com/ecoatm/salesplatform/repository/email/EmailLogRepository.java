package com.ecoatm.salesplatform.repository.email;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Rows in {@code status} whose {@code created_date} is older than
     * {@code cutoff} — used by T6's {@code EmailRetryWorker} to rescue
     * {@code PENDING} rows orphaned by an app crash (or kill) between the
     * log insert and the send attempt in
     * {@link com.ecoatm.salesplatform.service.email.EmailService#sendTemplated}.
     * The worker flips these to {@code FAILED} with
     * {@code next_attempt_at=now} so they re-enter the retry finder above.
     */
    List<EmailLog> findByStatusAndCreatedDateBefore(EmailStatus status, Instant cutoff);

    /**
     * Paged listing by status. Superseded for admin use by {@link #search}
     * below, which supports the full multi-field filter — kept here as the
     * simple building-block finder the {@code EmailRepositoryIT} baseline
     * case already exercises.
     */
    Page<EmailLog> findByStatus(EmailStatus status, Pageable pageable);

    /**
     * Filtered + paged listing backing T9's {@code GET /admin/email/log}.
     * Every param is independently nullable — {@code null} means "no filter
     * on this field," so a caller can mix any subset of {@code status}/
     * {@code from}/{@code to}/{@code templateKey}. {@code EmailLog} has no
     * other JPA entity of the same simple name (unlike {@code EmailTemplate}
     * — see the T8 {@code EmailManagementTemplate} disambiguation note in
     * the SDD progress log), so plain {@code FROM EmailLog} is unambiguous
     * here.
     *
     * <p>{@code from}/{@code to} are wrapped in {@code CAST(... AS timestamp)}
     * for their {@code IS NULL} check specifically. Without the cast, a
     * {@code null} {@code Instant} bound only through a bare
     * {@code :from IS NULL} predicate (no comparison operator in that
     * branch to give PostgreSQL a type to infer) trips the driver's
     * extended-protocol Describe phase with {@code PSQLException: ERROR:
     * could not determine data type of parameter $N} — confirmed against
     * real Postgres via {@code EmailRepositoryIT}, not just the mocked
     * {@code @WebMvcTest} slice. The cast is a no-op on the actual
     * comparison below (the OTHER occurrence of {@code :from}, e.g.
     * {@code l.createdDate >= :from}, is still typed from the
     * {@code TIMESTAMPTZ} column as usual) — it only pins the type for the
     * ambiguous standalone null-check. {@code status}/{@code templateKey}
     * do not need this: their enum/String binds don't hit the same
     * ambiguity (verified by the repository IT, which exercises each of the
     * four params as {@code null} in some call and non-null in another).
     */
    @Query("SELECT l FROM EmailLog l WHERE (:status IS NULL OR l.status = :status) "
         + "AND (CAST(:from AS timestamp) IS NULL OR l.createdDate >= :from) "
         + "AND (CAST(:to AS timestamp) IS NULL OR l.createdDate <= :to) "
         + "AND (:templateKey IS NULL OR l.templateKey = :templateKey)")
    Page<EmailLog> search(@Param("status") EmailStatus status, @Param("from") Instant from,
                          @Param("to") Instant to, @Param("templateKey") String templateKey, Pageable pageable);
}
