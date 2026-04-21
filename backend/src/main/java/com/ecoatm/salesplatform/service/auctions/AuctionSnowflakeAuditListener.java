package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.event.AuctionScheduledEvent;
import com.ecoatm.salesplatform.event.AuctionUnscheduledEvent;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Phase C no-op audit listener. Consumes {@link AuctionScheduledEvent} and
 * {@link AuctionUnscheduledEvent} after the originating business transaction
 * commits and logs what a future Phase F implementation would push to
 * Snowflake.
 *
 * <p>The actual Snowflake write is deliberately deferred — this listener only
 * establishes the wiring and guarantees the right lifecycle (post-commit +
 * off-thread) is in place so the Phase F change is one local edit.
 *
 * <p>The {@code snowflakeExecutor} bean is always available
 * (see {@link com.ecoatm.salesplatform.config.AsyncConfig}), so this component
 * does not need a conditional registration.
 */
@Component
public class AuctionSnowflakeAuditListener {

    private static final Logger log = LoggerFactory.getLogger(AuctionSnowflakeAuditListener.class);

    private final AuctionsFeatureConfigRepository featureConfigRepository;

    public AuctionSnowflakeAuditListener(AuctionsFeatureConfigRepository featureConfigRepository) {
        this.featureConfigRepository = featureConfigRepository;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionScheduled(AuctionScheduledEvent event) {
        if (!isPushEnabled()) {
            log.info("auction_audit: scheduled auctionId={} — no-op (feature flag off)", event.auctionId());
            return;
        }
        log.info("auction_audit: would push auctionId={} (scheduled) to Snowflake", event.auctionId());
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionUnscheduled(AuctionUnscheduledEvent event) {
        if (!isPushEnabled()) {
            log.info("auction_audit: unscheduled auctionId={} — no-op (feature flag off)", event.auctionId());
            return;
        }
        log.info("auction_audit: would push auctionId={} (unscheduled) to Snowflake", event.auctionId());
    }

    private boolean isPushEnabled() {
        return featureConfigRepository.findSingleton()
                .map(AuctionsFeatureConfig::isSendAuctionDataToSnowflake)
                .orElse(false);
    }
}
