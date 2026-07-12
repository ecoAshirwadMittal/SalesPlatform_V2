package com.ecoatm.salesplatform.event.rma;

import java.time.Instant;

/**
 * Spring event published by {@code RmaService.completeReview} after an RMA
 * review transitions to its terminal state ({@link RmaReviewOutcome#APPROVED}
 * or {@link RmaReviewOutcome#DECLINED}).
 *
 * <p>This is the single decoupling seam for gap-analysis #3 "Make RMA
 * Functional": the publish happens inside the {@code completeReview}
 * transaction, so every {@code @TransactionalEventListener(AFTER_COMMIT)}
 * subscriber fires only once the review has durably committed. Three
 * side-effects hang off this one event, each an independent listener so none
 * can roll back the review or block the admin UI response:
 * <ul>
 *   <li><b>Oracle RMA create</b> (this task) — {@code RmaOracleCreateListener}
 *       reacts to {@link RmaReviewOutcome#APPROVED} and writes the
 *       {@code oracle_*} columns.</li>
 *   <li><b>Approval email</b> (follow-on task C) — will subscribe to the same
 *       event.</li>
 *   <li><b>Snowflake sync</b> (follow-on task D) — will subscribe to the same
 *       event.</li>
 * </ul>
 * Subscribers reload the {@link com.ecoatm.salesplatform.model.pws.Rma} by
 * {@link #rmaId()} rather than carrying the aggregate on the event, mirroring
 * the partial-credit {@code ReviewCompletedEvent} pattern.
 *
 * @param rmaId            the {@code pws.rma.id} that completed review
 * @param outcome          terminal outcome — {@link RmaReviewOutcome#APPROVED}
 *                         (all/partial approve) or {@link RmaReviewOutcome#DECLINED}
 *                         (all declined); never an in-flight status
 * @param reviewedByUserId the {@code identity.users.id} of the reviewer who
 *                         completed the review (JWT-derived at the controller);
 *                         the Oracle payload's {@code originSystemUser} resolves
 *                         from this
 * @param occurredAt       wall-clock {@link Instant} the review completed
 */
public record RmaReviewCompletedEvent(
        Long rmaId,
        RmaReviewOutcome outcome,
        Long reviewedByUserId,
        Instant occurredAt) {
}
