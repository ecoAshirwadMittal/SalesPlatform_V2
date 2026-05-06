package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TargetPriceSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(TargetPriceSnowflakePushListener.class);

    private final TargetPriceSnowflakeWriter writer;
    private final Environment env;

    public TargetPriceSnowflakePushListener(TargetPriceSnowflakeWriter writer, Environment env) {
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTargetPriceRecalculated(TargetPriceRecalculatedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("snowflake.enabled", Boolean.class, false))) {
            log.info("[snowflake] disabled; skipping target-price push for event={}", event);
            return;
        }
        try {
            writer.pushTargetPrices(event.weekId(), event.closedRound() + 1);
            log.info("[snowflake] target-price pushed weekId={} targetRound={}",
                event.weekId(), event.closedRound() + 1);
        } catch (RuntimeException ex) {
            log.error("[snowflake] target-price push failed for event={}", event, ex);
            // future: write a FAILED row to integration.snowflake_sync_log
        }
    }
}
