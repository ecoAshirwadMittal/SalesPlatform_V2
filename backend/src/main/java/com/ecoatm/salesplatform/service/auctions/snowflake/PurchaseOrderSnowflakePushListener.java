package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Optional;

@Component
public class PurchaseOrderSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(
            PurchaseOrderSnowflakePushListener.class);

    private final PurchaseOrderRepository poRepo;
    private final PODetailRepository detailRepo;
    private final PurchaseOrderSnowflakeWriter writer;
    private final Environment env;

    public PurchaseOrderSnowflakePushListener(PurchaseOrderRepository poRepo,
                                              PODetailRepository detailRepo,
                                              PurchaseOrderSnowflakeWriter writer,
                                              Environment env) {
        this.poRepo = poRepo;
        this.detailRepo = detailRepo;
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChange(PurchaseOrderChangedEvent event) {
        if (!Boolean.parseBoolean(env.getProperty("po.sync.enabled", "true"))) {
            log.debug("po.sync.enabled=false; skipping push for PO {}",
                    event.purchaseOrderId());
            return;
        }
        try {
            switch (event.action()) {
                case UPSERT -> doUpsert(event.purchaseOrderId());
                case DELETE -> writer.delete(event.purchaseOrderId());
            }
        } catch (Exception ex) {
            log.warn("Snowflake push failed for PO {}; will be re-pushed on next upload",
                    event.purchaseOrderId(), ex);
        }
    }

    private void doUpsert(long purchaseOrderId) {
        Optional<PurchaseOrder> opt = poRepo.findByIdWithDetails(purchaseOrderId);
        if (opt.isEmpty()) {
            log.warn("Snowflake push: PO {} no longer exists; skipping", purchaseOrderId);
            return;
        }
        writer.upsert(toPayload(opt.get()));
    }

    static PurchaseOrderSnowflakePayload toPayload(PurchaseOrder po) {
        var weekFrom = new PurchaseOrderSnowflakePayload.WeekRef(
                po.getWeekFrom().getId(),
                po.getWeekFrom().getYear(),
                po.getWeekFrom().getWeekNumber());
        var weekTo = new PurchaseOrderSnowflakePayload.WeekRef(
                po.getWeekTo().getId(),
                po.getWeekTo().getYear(),
                po.getWeekTo().getWeekNumber());
        var details = po.getDetails().stream().map(d -> mapDetail(d)).toList();
        return new PurchaseOrderSnowflakePayload(
                po.getId(),
                po.getLegacyId() == null ? 0L : po.getLegacyId(),
                weekFrom, weekTo,
                po.getWeekRangeLabel(),
                po.getTotalRecords(),
                Instant.now(),
                details);
    }

    private static PurchaseOrderSnowflakePayload.DetailPayload mapDetail(PODetail d) {
        // Null-safe id handling: in the unit-test stub PODetail rows are constructed
        // without persisting, so getId() can be null. Production rows always have an id.
        return new PurchaseOrderSnowflakePayload.DetailPayload(
                d.getId() == null ? 0L : d.getId(),
                d.getLegacyId() == null ? 0L : d.getLegacyId(),
                d.getProductId(), d.getGrade(), d.getModelName(),
                d.getPrice(), d.getQtyCap(),
                d.getPriceFulfilled(), d.getQtyFulfilled(),
                d.getBuyerCode().getCode(), d.getTempBuyerCode());
    }
}
