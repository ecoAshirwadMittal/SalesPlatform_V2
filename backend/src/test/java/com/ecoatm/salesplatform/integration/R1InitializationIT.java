package com.ecoatm.salesplatform.integration;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import jakarta.persistence.EntityManager;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end verification of the R1 init chain: publishing a
 * {@link RoundStartedEvent} inside a committed tx triggers the
 * {@code @TransactionalEventListener(AFTER_COMMIT)} + {@code @Async}
 * {@link com.ecoatm.salesplatform.service.auctions.r1init.R1InitListener},
 * which opens {@code REQUIRES_NEW} and runs
 * {@link com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService}
 * against real Postgres (Flyway V1–V72 applied, V18 seed providing the
 * ~579 active Wholesale/Data_Wipe buyer codes that the service fans
 * out over plus the singleton {@code AuctionsFeatureConfig} with
 * {@code minimumAllowedBid = 2.00}).
 *
 * <p>The class is intentionally <strong>not</strong> {@code @Transactional}:
 * the listener opens its own {@code REQUIRES_NEW} tx, which would not see
 * rows committed by a surrounding test tx. Instead we track the ids we
 * insert and delete them in {@code @AfterEach}; the V72 FK cascade on
 * {@code qualified_buyer_codes.scheduling_auction_id} cleans up the QBCs
 * the service created when we delete the parent auction.
 *
 * <p>Awaitility bridges the test thread and {@code snowflakeExecutor} —
 * the listener is async, so without polling the test would assert before
 * the handler ran.
 */
@TestPropertySource(properties = {
        "auctions.r1-init.enabled=true",
        "auctions.lifecycle.enabled=false"
})
class R1InitializationIT extends PostgresIntegrationTest {

    @Autowired private AuctionRepository auctionRepo;
    @Autowired private SchedulingAuctionRepository saRepo;
    @Autowired private AggregatedInventoryRepository aggInvRepo;
    @Autowired private BuyerRepository buyerRepo;
    @Autowired private BuyerCodeRepository buyerCodeRepo;
    @Autowired private WeekRepository weekRepo;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private TransactionTemplate tx;
    @Autowired private EntityManager em;

    private final List<Long> createdAuctionIds = new ArrayList<>();
    private final List<Long> createdAggInvIds = new ArrayList<>();
    private final List<Long> createdBuyerCodeIds = new ArrayList<>();
    private final List<Long> createdBuyerIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        // Delete aggregated inventory first — no FKs pointing at it from our rows.
        for (Long id : createdAggInvIds) {
            try { aggInvRepo.deleteById(id); } catch (Exception ignored) {}
        }
        // Delete junctions by our buyer code ids then the buyer codes themselves.
        tx.executeWithoutResult(s -> {
            for (Long bcId : createdBuyerCodeIds) {
                try {
                    em.createNativeQuery(
                            "DELETE FROM buyer_mgmt.buyer_code_buyers WHERE buyer_code_id = :id")
                            .setParameter("id", bcId).executeUpdate();
                } catch (Exception ignored) {}
            }
        });
        for (Long id : createdBuyerCodeIds) {
            try { buyerCodeRepo.deleteById(id); } catch (Exception ignored) {}
        }
        for (Long id : createdBuyerIds) {
            try { buyerRepo.deleteById(id); } catch (Exception ignored) {}
        }
        // Deleting the auction cascades to scheduling_auctions and then to
        // qualified_buyer_codes (both FK cascades established by V58 + V72).
        for (Long id : createdAuctionIds) {
            try { auctionRepo.deleteById(id); } catch (Exception ignored) {}
        }
        createdAggInvIds.clear();
        createdBuyerCodeIds.clear();
        createdBuyerIds.clear();
        createdAuctionIds.clear();
    }

    @Test
    @DisplayName("RoundStartedEvent → R1 init clamps target prices and seeds QBCs end-to-end")
    void roundStartedEvent_triggersR1InitEndToEnd() {
        long weekId = firstWeekId();

        Auction a = persistAuction(AuctionStatus.Started, weekId,
                "IT R1-init " + System.nanoTime());
        SchedulingAuction sa = persistRound(a.getId(), 1, SchedulingAuctionStatus.Started,
                Instant.now().minusSeconds(10), Instant.now().plusSeconds(3600));

        // Floor is 2.00 (V18 seed → AuctionsFeatureConfig.minimumAllowedBid = 2.00000000).
        long belowId = seedAggInv(weekId, "below", new BigDecimal("1.00"), 10,
                new BigDecimal("1.50"), 5);
        long aboveId = seedAggInv(weekId, "above", new BigDecimal("5.00"), 10,
                new BigDecimal("5.50"), 5);

        // Three active qualifying codes we fully control, plus two that must NOT
        // be qualified. The V18 seed already contributes ~579 other active
        // Wholesale/Data_Wipe codes, so we assert presence/absence of our ids
        // rather than an exact count.
        long wsActiveId   = seedBuyerCode("WS-A-" + System.nanoTime(), "Wholesale", BuyerStatus.Active, false);
        long dwActiveId   = seedBuyerCode("DW-A-" + System.nanoTime(), "Data_Wipe", BuyerStatus.Active, false);
        long wsDisabledId = seedBuyerCode("WS-D-" + System.nanoTime(), "Wholesale", BuyerStatus.Disabled, false);
        long dwSoftDelId  = seedBuyerCode("DW-S-" + System.nanoTime(), "Data_Wipe", BuyerStatus.Active, true);
        // Purchasing_Order is a valid non-qualifying type (chk_buyer_code_type allows
        // Wholesale, Data_Wipe, Purchasing_Order_Data_Wipe, Purchasing_Order); the
        // service filter selects only Wholesale/Data_Wipe, so this row must not
        // appear in the resulting QBC set.
        long nonQualId    = seedBuyerCode("PO-"   + System.nanoTime(), "Purchasing_Order", BuyerStatus.Active, false);

        // Publish inside a committing tx — the listener uses AFTER_COMMIT,
        // which is a no-op if fired outside a transaction (fallbackExecution=false default).
        tx.executeWithoutResult(s ->
                eventPublisher.publishEvent(
                        new RoundStartedEvent(sa.getId(), 1, a.getId(), weekId)));

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    @SuppressWarnings("unchecked")
                    List<Number> rows = em.createNativeQuery("""
                            SELECT buyer_code_id
                              FROM buyer_mgmt.qualified_buyer_codes
                             WHERE scheduling_auction_id = :saId
                            """)
                            .setParameter("saId", sa.getId())
                            .getResultList();
                    List<Long> buyerCodeIds = rows.stream().map(Number::longValue).toList();
                    assertThat(buyerCodeIds).contains(wsActiveId, dwActiveId);
                    assertThat(buyerCodeIds).doesNotContain(wsDisabledId, dwSoftDelId, nonQualId);
                });

        // Target price clamps ran — below-floor rows lifted to 2.00, above-floor rows preserved.
        assertThat(aggInvRepo.findById(belowId).orElseThrow().getAvgTargetPrice())
                .isEqualByComparingTo("2.00");
        assertThat(aggInvRepo.findById(belowId).orElseThrow().getDwAvgTargetPrice())
                .isEqualByComparingTo("2.00");
        assertThat(aggInvRepo.findById(aboveId).orElseThrow().getAvgTargetPrice())
                .isEqualByComparingTo("5.00");
        assertThat(aggInvRepo.findById(aboveId).orElseThrow().getDwAvgTargetPrice())
                .isEqualByComparingTo("5.50");
    }

    private long firstWeekId() {
        return weekRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("mdm.week is empty — V65 seed didn't run"))
                .getId();
    }

    private Auction persistAuction(AuctionStatus status, long weekId, String title) {
        Auction a = new Auction();
        a.setAuctionTitle(title);
        a.setAuctionStatus(status);
        a.setWeekId(weekId);
        a.setCreatedDate(Instant.now());
        a.setChangedDate(Instant.now());
        a.setCreatedBy("it");
        a.setUpdatedBy("it");
        Auction saved = auctionRepo.save(a);
        createdAuctionIds.add(saved.getId());
        return saved;
    }

    private SchedulingAuction persistRound(Long auctionId, int round,
                                           SchedulingAuctionStatus status,
                                           Instant start, Instant end) {
        SchedulingAuction r = new SchedulingAuction();
        r.setAuctionId(auctionId);
        r.setRound(round);
        r.setRoundStatus(status);
        r.setStartDatetime(start);
        r.setEndDatetime(end);
        r.setCreatedDate(Instant.now());
        r.setChangedDate(Instant.now());
        r.setCreatedBy("it");
        r.setUpdatedBy("it");
        return saRepo.save(r);
    }

    private long seedAggInv(long weekId, String ecoid, BigDecimal nonDwPrice, int nonDwQty,
                            BigDecimal dwPrice, int dwQty) {
        AggregatedInventory row = new AggregatedInventory();
        row.setWeekId(weekId);
        row.setEcoid2(ecoid + "-" + System.nanoTime());
        row.setMergedGrade("Good");
        row.setDatawipe(false);
        row.setTotalQuantity(nonDwQty);
        row.setDwTotalQuantity(dwQty);
        row.setAvgTargetPrice(nonDwPrice);
        row.setDwAvgTargetPrice(dwPrice);
        row.setCreatedDate(Instant.now());
        row.setChangedDate(Instant.now());
        AggregatedInventory saved = aggInvRepo.save(row);
        createdAggInvIds.add(saved.getId());
        return saved.getId();
    }

    /**
     * Creates a Buyer + BuyerCode pair linked via the {@code buyer_code_buyers}
     * junction. The R1 init query requires the junction because it joins back
     * to {@code buyers} to filter on {@code status = 'Active'}.
     */
    private long seedBuyerCode(String code, String type, BuyerStatus buyerStatus, boolean softDeleteCode) {
        Buyer buyer = new Buyer();
        buyer.setCompanyName(code + "-co");
        buyer.setStatus(buyerStatus);
        buyer.setCreatedDate(LocalDateTime.now());
        buyer.setChangedDate(LocalDateTime.now());
        Buyer savedBuyer = buyerRepo.save(buyer);
        createdBuyerIds.add(savedBuyer.getId());

        BuyerCode bc = new BuyerCode();
        bc.setCode(code);
        bc.setBuyerCodeType(type);
        bc.setStatus("Active");
        bc.setSoftDelete(softDeleteCode);
        bc.setCreatedDate(LocalDateTime.now());
        bc.setChangedDate(LocalDateTime.now());
        BuyerCode savedCode = buyerCodeRepo.save(bc);
        createdBuyerCodeIds.add(savedCode.getId());

        final Long buyerId = savedBuyer.getId();
        final Long codeId = savedCode.getId();
        tx.executeWithoutResult(s -> em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                VALUES (:codeId, :buyerId)
                """)
                .setParameter("codeId", codeId)
                .setParameter("buyerId", buyerId)
                .executeUpdate());

        return codeId;
    }
}
