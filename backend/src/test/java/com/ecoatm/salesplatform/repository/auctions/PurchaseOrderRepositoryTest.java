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

        var page = poRepo.findActiveOnDate(currentWeekId, PageRequest.of(0, 50));
        assertThat(page.getContent()).extracting(PurchaseOrder::getId)
                .contains(activeNow.getId());
    }
}
