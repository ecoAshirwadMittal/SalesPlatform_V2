package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PurchaseOrderTest {

    @PersistenceContext EntityManager em;

    @Test
    void persistAndReloadPurchaseOrder() {
        Week from = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        Week to = from;
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(from);
        po.setWeekTo(to);
        po.setWeekRangeLabel("TEST-RANGE");
        po.setTotalRecords(0);
        em.persist(po);
        em.flush();
        em.clear();

        PurchaseOrder reloaded = em.find(PurchaseOrder.class, po.getId());
        assertThat(reloaded.getWeekRangeLabel()).isEqualTo("TEST-RANGE");
        assertThat(reloaded.getValidYearWeek()).isTrue();
        assertThat(reloaded.getCreatedDate()).isNotNull();
    }

    @Test
    void persistAndReloadPoDetail() {
        Week w = em.find(Week.class, em.createQuery(
                "SELECT MIN(w.id) FROM Week w", Long.class).getSingleResult());
        BuyerCode bc = em.find(BuyerCode.class, em.createQuery(
                "SELECT MIN(b.id) FROM BuyerCode b", Long.class).getSingleResult());
        PurchaseOrder po = new PurchaseOrder();
        po.setWeekFrom(w); po.setWeekTo(w); po.setWeekRangeLabel("X");
        em.persist(po);

        PODetail d = new PODetail();
        d.setPurchaseOrder(po);
        d.setBuyerCode(bc);
        d.setProductId("12345");
        d.setGrade("A_YYY");
        d.setPrice(new BigDecimal("99.99"));
        em.persist(d);
        em.flush();
        em.clear();

        PODetail reloaded = em.find(PODetail.class, d.getId());
        assertThat(reloaded.getProductId()).isEqualTo("12345");
        assertThat(reloaded.getPurchaseOrder().getId()).isEqualTo(po.getId());
    }

    @Test
    void lifecycleStateDeriveDraft() {
        Week future = stubWeek(LocalDate.now().plusDays(30), LocalDate.now().plusDays(36));
        Week futureEnd = stubWeek(LocalDate.now().plusDays(37), LocalDate.now().plusDays(43));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), future, futureEnd))
                .isEqualTo(PurchaseOrderLifecycleState.DRAFT);
    }

    @Test
    void lifecycleStateDeriveActive() {
        Week from = stubWeek(LocalDate.now().minusDays(2), LocalDate.now().plusDays(4));
        Week to = stubWeek(LocalDate.now().plusDays(5), LocalDate.now().plusDays(11));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), from, to))
                .isEqualTo(PurchaseOrderLifecycleState.ACTIVE);
    }

    @Test
    void lifecycleStateDeriveClosed() {
        Week from = stubWeek(LocalDate.now().minusDays(20), LocalDate.now().minusDays(14));
        Week to = stubWeek(LocalDate.now().minusDays(13), LocalDate.now().minusDays(7));
        assertThat(PurchaseOrderLifecycleState.derive(LocalDate.now(), from, to))
                .isEqualTo(PurchaseOrderLifecycleState.CLOSED);
    }

    private static Week stubWeek(LocalDate start, LocalDate end) {
        Week w = new Week();
        w.setWeekStartDateTime(start.atStartOfDay().atZone(java.time.ZoneOffset.UTC).toInstant());
        w.setWeekEndDateTime(end.atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant());
        return w;
    }
}
