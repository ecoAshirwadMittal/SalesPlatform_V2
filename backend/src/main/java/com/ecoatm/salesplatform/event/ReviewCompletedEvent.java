package com.ecoatm.salesplatform.event;

import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;

import java.time.Instant;

/**
 * Spring event published by {@code AdminCreditRequestService.completeReview}
 * after a partial-credit review transitions to its terminal state
 * (APPROVED or DECLINED).
 *
 * <p>Sprint 3 publishes the event; the email listener (Sprint 3 chunk 8)
 * subscribes asynchronously to send the buyer-facing "Review Completed"
 * email. Decoupling the publish from the send keeps {@code completeReview}
 * fast + deterministic for the admin UI roundtrip.
 *
 * @param requestId       the {@code credit_requests.id} that completed review
 * @param outcome         terminal status — either {@link SystemStatus#APPROVED}
 *                        or {@link SystemStatus#DECLINED}; never the in-flight
 *                        DRAFT/PENDING_APPROVAL/UNDER_REVIEW values
 * @param reviewerUserId  the {@code identity.users.id} of the reviewer who
 *                        confirmed completion; the email subject line uses
 *                        this to attribute the action
 * @param occurredAt      wall-clock {@link Instant} of the publish; the
 *                        listener stamps the outbound email
 */
public record ReviewCompletedEvent(
        Long requestId,
        SystemStatus outcome,
        Long reviewerUserId,
        Instant occurredAt) {
}
