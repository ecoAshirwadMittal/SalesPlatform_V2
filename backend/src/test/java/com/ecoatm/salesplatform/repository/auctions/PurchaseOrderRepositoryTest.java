package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PurchaseOrderRepositoryTest {

    @Autowired PurchaseOrderRepository poRepo;
    @Autowired PODetailRepository detailRepo;
    @PersistenceContext EntityManager em;

    @Test
    void deleteAllByPurchaseOrderIdRemovesAllRows() {
        Week w = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        BuyerCode bc = em.find(BuyerCode.class, em.createQuery(
                "SELECT MIN(b.id) FROM BuyerCode b", Long.class).getSingleResult());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(w); po.setWeekTo(w); po.setWeekRangeLabel("REPO-TEST");
        em.persist(po);
        for (int i = 0; i < 5; i++) {
            PODetail d = new PODetail();
            d.setPurchaseOrder(po);
            d.setBuyerCode(bc);
            d.setProductId("P" + i);
            d.setGrade("A_YYY");
            d.setPrice(new BigDecimal("10.00"));
            em.persist(d);
        }
        em.flush();

        long deleted = detailRepo.deleteAllByPurchaseOrderId(po.getId());
        em.flush();

        assertThat(deleted).isEqualTo(5);
        assertThat(detailRepo.countByPurchaseOrderId(po.getId())).isZero();
    }

    @Test
    void findOverlappingWeekFiltersCorrectly() {
        Long currentWeekId = em.createQuery(
                "SELECT w.id FROM Week w WHERE w.weekStartDateTime <= CURRENT_TIMESTAMP "
              + "AND w.weekEndDateTime >= CURRENT_TIMESTAMP", Long.class)
                .setMaxResults(1).getSingleResult();
        Week current = em.find(Week.class, currentWeekId);
        PurchaseOrder activeNow = new PurchaseOrder();
        activeNow.setWeekFrom(current); activeNow.setWeekTo(current);
        activeNow.setWeekRangeLabel("ACTIVE-NOW");
        em.persist(activeNow);
        em.flush();

        // findActiveOnDate compares the BUSINESS weekId (mdm.week.week_id), so
        // the caller passes current.getWeekId(), not the surrogate id.
        var page = poRepo.findActiveOnDate(current.getWeekId(), PageRequest.of(0, 50));
        assertThat(page.getContent()).extracting(PurchaseOrder::getId)
                .contains(activeNow.getId());
    }

    @Test
    void findActiveOnDateMatchesByBusinessWeekIdNotSurrogate() {
        // findActiveOnDate must decide PO lifecycle by the BUSINESS weekId
        // (mdm.week.week_id, chronological), NOT the non-chronological surrogate
        // mdm.week.id. Persist order fixes ascending surrogate ids; the business
        // weekIds are chosen so surrogate order != chronological order:
        //   persist #1 decoyFrom biz 9000010 -> surrogate s1 (smallest)
        //   persist #2 coverFrom biz 9000020 -> surrogate s2
        //   persist #3 coverTo   biz 9000022 -> surrogate s3
        //   persist #4 target    biz 9000021 -> surrogate s4
        //   persist #5 decoyTo   biz 9000011 -> surrogate s5 (largest)
        Week decoyFrom = persistWeek(9000010L);
        Week coverFrom = persistWeek(9000020L);
        Week coverTo   = persistWeek(9000022L);
        Week target    = persistWeek(9000021L);
        Week decoyTo   = persistWeek(9000011L);

        // Surrogate order is scrambled vs business order:
        //   covering surrogate range [s2..s3] does NOT contain target surrogate s4
        //     -> the pre-fix surrogate range test would MISS the covering PO.
        //   decoy surrogate range [s1..s5] DOES contain target surrogate s4
        //     -> the pre-fix surrogate range test would WRONGLY include the decoy.
        assertThat(coverTo.getId()).isLessThan(target.getId());     // s3 < s4
        assertThat(decoyFrom.getId()).isLessThan(target.getId());   // s1 < s4
        assertThat(decoyTo.getId()).isGreaterThan(target.getId());  // s5 > s4

        // Covering PO: business [9000020..9000022] CONTAINS target 9000021.
        PurchaseOrder covering = persistPo(coverFrom, coverTo, "COVER");
        // Decoy PO: business [9000010..9000011] does NOT contain target 9000021.
        PurchaseOrder decoy = persistPo(decoyFrom, decoyTo, "DECOY");

        var page = poRepo.findActiveOnDate(target.getWeekId(), PageRequest.of(0, 50));

        assertThat(page.getContent()).extracting(PurchaseOrder::getId)
                .contains(covering.getId())       // business range covers the target week
                .doesNotContain(decoy.getId());   // business range does not cover it
    }

    private Week persistWeek(long businessWeekId) {
        Week w = new Week();
        w.setWeekId(businessWeekId);
        w.setYear((int) (businessWeekId / 100));
        w.setWeekNumber((int) (businessWeekId % 100));
        w.setWeekDisplay("BIZ-" + businessWeekId);
        // week_start/end are NOT NULL in the DB and unused by findActiveOnDate;
        // set them (offset by weekId so they stay chronological) to satisfy the
        // constraint.
        Instant start = Instant.parse("2030-01-01T00:00:00Z")
                .plus(businessWeekId % 1000, ChronoUnit.DAYS);
        w.setWeekStartDateTime(start);
        w.setWeekEndDateTime(start.plus(6, ChronoUnit.DAYS));
        em.persist(w);
        em.flush(); // force the IDENTITY surrogate id so persist order == id order
        return w;
    }

    private PurchaseOrder persistPo(Week from, Week to, String label) {
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(from);
        po.setWeekTo(to);
        po.setWeekRangeLabel(label);
        em.persist(po);
        em.flush();
        return po;
    }
}
