package com.ecoatm.salesplatform.event.partialcredit;

import java.time.Instant;

/**
 * Spring event published by {@code CreditRequestService.submit} after a
 * partial-credit request transitions DRAFT → PENDING_APPROVAL.
 *
 * <p>Gap 2.5 Task 1: the submission-confirmation email listener
 * ({@code CreditRequestSubmittedEmailListener}) subscribes asynchronously
 * ({@code AFTER_COMMIT} + {@code @Async}) to send the buyer-facing "request
 * submitted" email. Decoupling the publish from the send keeps {@code submit}
 * fast + deterministic for the wizard's response. This event also establishes
 * the submit-event seam that a later accounting-email task reuses — hence it
 * carries facts only ({@code requestId} + submitter + timestamp), leaving the
 * recipient / rendering decisions to each subscribing listener.
 *
 * @param requestId         the {@code credit_requests.id} that was submitted
 * @param submittedByUserId the {@code identity.users.id} of the buyer (or
 *                          sales-rep, for on-behalf) who submitted
 * @param occurredAt        wall-clock {@link Instant} of the publish
 */
public record CreditRequestSubmittedEvent(
        Long requestId,
        Long submittedByUserId,
        Instant occurredAt) {
}
