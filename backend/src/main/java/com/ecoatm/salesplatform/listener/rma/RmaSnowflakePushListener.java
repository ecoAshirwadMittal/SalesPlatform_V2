package com.ecoatm.salesplatform.listener.rma;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.rma.RmaSnowflakePayload;
import com.ecoatm.salesplatform.service.rma.RmaSnowflakeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;

/**
 * Pushes an RMA to Snowflake once its review commits — the event-driven
 * replacement for the legacy {@code SUB_SendRMADetailsToSnowflake} call inside
 * {@code ACT_RMADetails_CompleteReview}. Decoupled so the Snowflake round-trip
 * runs off the admin request thread and can never roll the review back.
 *
 * <p>Same {@code AFTER_COMMIT} + {@code @Async} shape as the PO / recalc
 * Snowflake listeners and the RMA Oracle-create listener: it fires only after
 * the review-completion transaction has durably committed, and runs on the
 * shared {@link AsyncConfig#SNOWFLAKE_EXECUTOR} pool.
 *
 * <p><b>Pushes on any completion.</b> Legacy {@code ACT_RMADetails_CompleteReview}
 * calls {@code SUB_SendRMADetailsToSnowflake} on both the approved (post
 * Oracle-create) and declined branches, so this listener does <em>not</em> gate
 * on {@code outcome} — unlike {@code RmaOracleCreateListener}, which acts only on
 * APPROVED. The reloaded RMA carries whatever terminal + {@code oracle_*} state
 * was written, so the Snowflake row reflects the true outcome either way.
 *
 * <p><b>Failure never affects the review.</b> The originating review has already
 * committed in a separate transaction; any exception here (a missing RMA, a
 * writer/infrastructure failure) is caught and logged, never rethrown. The next
 * completion re-pushes; there is no retry queue for this side-effect (matching
 * the PO listener, which relies on the next upload to re-push).
 *
 * <p>Gated by {@code rma.sync.enabled} (default {@code true}). When disabled the
 * listener still subscribes so it can log the intent — flipping the flag needs
 * no bean restart. Writer selection ({@code logging} default / {@code jdbc}) is
 * independent, via {@code rma.sync.writer} on the writer beans.
 */
@Component
public class RmaSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(RmaSnowflakePushListener.class);

    private final RmaSnowflakeWriter writer;
    private final RmaRepository rmaRepository;
    private final RmaItemRepository rmaItemRepository;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final boolean enabled;

    public RmaSnowflakePushListener(RmaSnowflakeWriter writer,
                                    RmaRepository rmaRepository,
                                    RmaItemRepository rmaItemRepository,
                                    BuyerCodeLookupService buyerCodeLookup,
                                    @Value("${rma.sync.enabled:true}") boolean enabled) {
        this.writer = writer;
        this.rmaRepository = rmaRepository;
        this.rmaItemRepository = rmaItemRepository;
        this.buyerCodeLookup = buyerCodeLookup;
        this.enabled = enabled;
    }

    /**
     * React to a committed {@link RmaReviewCompletedEvent} by snapshotting the
     * RMA and pushing it to Snowflake. Runs on {@link AsyncConfig#SNOWFLAKE_EXECUTOR}.
     * All exceptions are caught and logged — a failure here must never affect the
     * already-final review.
     */
    @Async(AsyncConfig.SNOWFLAKE_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRmaReviewCompleted(RmaReviewCompletedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error("RMA Snowflake push failed for rmaId={}: {}",
                    event.rmaId(), ex.getMessage(), ex);
        }
    }

    private void handle(RmaReviewCompletedEvent event) {
        if (!enabled) {
            log.info("[RmaSnowflakePushListener] (disabled) would push RMA to Snowflake for rmaId={}",
                    event.rmaId());
            return;
        }
        Long rmaId = event.rmaId();
        if (rmaId == null) {
            log.warn("RmaReviewCompletedEvent published with null rmaId — skipping Snowflake push");
            return;
        }
        Rma rma = rmaRepository.findById(rmaId).orElse(null);
        if (rma == null) {
            log.warn("RMA {} no longer exists; skipping Snowflake push", rmaId);
            return;
        }
        // Items loaded via a separate query (not the lazy association) so the
        // snapshot builds cleanly on the async thread with open-in-view off,
        // mirroring RmaOracleService.
        List<RmaItem> items = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId);
        writer.push(toPayload(rma, items, resolveBuyerCode(rma.getBuyerCodeId())));
        log.info("RMA {} pushed to Snowflake ({} items, status={})",
                rma.getNumber(), items.size(), rma.getSystemStatus());
    }

    /** Resolve the human buyer-code string; best-effort — a null id yields null. */
    private String resolveBuyerCode(Long buyerCodeId) {
        return buyerCodeId == null ? null : buyerCodeLookup.findCodeById(buyerCodeId);
    }

    static RmaSnowflakePayload toPayload(Rma rma, List<RmaItem> items, String buyerCode) {
        List<RmaSnowflakePayload.ItemPayload> itemPayloads = items.stream()
                .map(RmaSnowflakePushListener::mapItem)
                .toList();
        return new RmaSnowflakePayload(
                rma.getId(),
                rma.getNumber(),
                rma.getBuyerCodeId(),
                buyerCode,
                rma.getSystemStatus(),
                rma.getOracleRmaStatus(),
                rma.getOracleNumber(),
                rma.getOracleId(),
                rma.getOracleHttpCode(),
                rma.getIsSuccessful(),
                rma.getRequestSkus(),
                rma.getRequestQty(),
                rma.getRequestSalesTotal(),
                rma.getApprovedSkus(),
                rma.getApprovedQty(),
                rma.getApprovedSalesTotal(),
                rma.getApprovedCount(),
                rma.getDeclinedCount(),
                rma.getReviewedByUserId(),
                rma.getSubmittedByUserId(),
                rma.getSubmittedDate(),
                rma.getApprovalDate(),
                rma.getReviewCompletedOn(),
                Instant.now(),
                itemPayloads);
    }

    private static RmaSnowflakePayload.ItemPayload mapItem(RmaItem item) {
        return new RmaSnowflakePayload.ItemPayload(
                item.getId() == null ? 0L : item.getId(),
                item.getDeviceId(),
                item.getImei(),
                item.getOrderNumber(),
                item.getSalePrice(),
                item.getReturnReason(),
                item.getStatus(),
                item.getStatusDisplay(),
                item.getDeclineReason());
    }
}
