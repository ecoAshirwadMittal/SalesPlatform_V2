package com.ecoatm.salesplatform.dto.email;

import com.ecoatm.salesplatform.model.email.EmailLog;
import com.ecoatm.salesplatform.model.email.EmailStatus;

import java.time.Instant;

/**
 * Admin-facing projection of {@link EmailLog} (unified {@code email.log}
 * store, V92) for the Task 9 delivery-log endpoints. The same shape backs
 * list, detail, and resend responses — including the rendered {@code
 * contentHtml} snapshot on list rows too, rather than splitting a lean list
 * row from a richer detail row (Phase 1 keeps a single DTO; revisit if list
 * payload size ever becomes a concern).
 */
public record EmailLogView(
        Long id,
        String templateKey,
        String fromAddress,
        String toAddress,
        String cc,
        String bcc,
        String subject,
        String contentHtml,
        EmailStatus status,
        String errorMessage,
        Integer retryCount,
        Instant nextAttemptAt,
        String sourceModule,
        Long sourceId,
        Instant sentDate,
        Instant createdDate) {

    public static EmailLogView from(EmailLog log) {
        return new EmailLogView(
                log.getId(),
                log.getTemplateKey(),
                log.getFromAddress(),
                log.getToAddress(),
                log.getCc(),
                log.getBcc(),
                log.getSubject(),
                log.getContentHtml(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getRetryCount(),
                log.getNextAttemptAt(),
                log.getSourceModule(),
                log.getSourceId(),
                log.getSentDate(),
                log.getCreatedDate());
    }
}
