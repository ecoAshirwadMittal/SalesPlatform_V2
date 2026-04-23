package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.List;

@Component
public class ReserveBidSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSnowflakePushListener.class);

    private final ReserveBidRepository repo;
    private final ReserveBidSnowflakeWriter writer;
    private final Environment env;

    public ReserveBidSnowflakePushListener(ReserveBidRepository repo,
                                           ReserveBidSnowflakeWriter writer,
                                           Environment env) {
        this.repo = repo;
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(ReserveBidChangedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("eb.sync.enabled", Boolean.class, true))) {
            log.info("[eb-push] disabled; would {} ids={}", event.action(), event.changedIds());
            return;
        }
        try {
            if (event.action() == ReserveBidChangedEvent.Action.UPSERT) {
                List<ReserveBid> rows = repo.findAllById(event.changedIds());
                List<ReserveBidSnowflakePayload.Row> payloadRows = rows.stream()
                        .map(rb -> new ReserveBidSnowflakePayload.Row(
                                rb.getProductId(), rb.getGrade(), rb.getBrand(), rb.getModel(),
                                rb.getBid(), rb.getLastUpdateDatetime(),
                                rb.getLastAwardedMinPrice(), rb.getLastAwardedWeek()))
                        .toList();
                writer.upsert(new ReserveBidSnowflakePayload(payloadRows, actingUser(), "UPSERT"));
            } else {
                writer.delete(new ReserveBidSnowflakePayload(Collections.emptyList(), actingUser(), "DELETE"));
            }
        } catch (Exception ex) {
            log.error("[eb-push] failed action={} ids={} - swallowed so Postgres tx stands",
                    event.action(), event.changedIds(), ex);
        }
    }

    private static String actingUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
