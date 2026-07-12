package com.ecoatm.salesplatform.event.buyermgmt;

import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;

import java.time.Instant;

/**
 * Spring event published by {@code QualifiedBuyerCodeAdminService.updateIncluded}
 * after an admin successfully overrides a Qualified Buyer Code's
 * {@code included} flag (which also forces {@code qualification_type=Manual}).
 *
 * <p>This is the decoupling seam for gap-analysis 2.4 sub-feature 2 (the
 * modern {@code _New} single path). The publish happens inside the same
 * transaction that commits the override, so a
 * {@code @TransactionalEventListener(AFTER_COMMIT)} subscriber fires only
 * once the override has durably committed — mirroring the RMA
 * {@code RmaReviewCompletedEvent} and partial-credit {@code ReviewCompletedEvent}
 * patterns.
 *
 * <p>The event carries the <em>facts</em> only; it does not decide whether an
 * email is sent. The manual-qualification email listener (Task 4) subscribes to
 * this event and applies the legacy {@code NF_OnIncludedChanged_New} condition
 * itself — the email fires only when {@code roundStatus == Started &&
 * included == true}. Every successful override publishes this event regardless
 * of {@code included} / {@code roundStatus} so the listener owns that decision.
 * A {@code Closed}-round override never reaches this publish (it is rejected by
 * the round-status guard before any mutation), so {@code roundStatus} here is
 * always {@code Scheduled}, {@code Started}, or {@code Unscheduled} — never
 * {@code Closed}.
 *
 * @param qualifiedBuyerCodeId the {@code buyer_mgmt.qualified_buyer_codes.id}
 *                             whose {@code included} flag was overridden
 * @param buyerCodeId          the {@code buyer_mgmt.buyer_codes.id} the QBC row
 *                             points at — the listener resolves email recipients
 *                             from this
 * @param schedulingAuctionId  the {@code auctions.scheduling_auctions.id} the QBC
 *                             belongs to
 * @param included             the new {@code included} value after the override
 * @param roundStatus          the SchedulingAuction's round status at override
 *                             time; the listener emails only when this is
 *                             {@link SchedulingAuctionStatus#Started}
 * @param changedByUserId      the {@code identity.users.id} of the admin who made
 *                             the change (JWT-derived at the controller)
 * @param occurredAt           wall-clock {@link Instant} the override committed
 */
public record QualificationOverriddenEvent(
        Long qualifiedBuyerCodeId,
        Long buyerCodeId,
        Long schedulingAuctionId,
        boolean included,
        SchedulingAuctionStatus roundStatus,
        Long changedByUserId,
        Instant occurredAt) {
}
