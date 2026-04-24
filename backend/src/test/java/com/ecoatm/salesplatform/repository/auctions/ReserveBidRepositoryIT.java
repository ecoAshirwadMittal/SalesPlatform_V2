package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReserveBidRepositoryIT {

    @Autowired ReserveBidRepository repo;
    @Autowired ReserveBidAuditRepository auditRepo;
    @Autowired ReserveBidSyncRepository syncRepo;

    @Test
    void findByProductIdAndGrade() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("77001");
        rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("10.0000"));
        repo.save(rb);

        Optional<ReserveBid> found = repo.findByProductIdAndGrade("77001", "A_YYY");
        assertThat(found).isPresent();
        assertThat(found.get().getBid()).isEqualByComparingTo("10.00");
    }

    @Test
    void syncSingletonReachable() {
        Optional<ReserveBidSync> s = syncRepo.findFirstByOrderByIdAsc();
        assertThat(s).isPresent();
    }

    @Test
    void auditByReserveBidId() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("77002");
        rb.setGrade("B_NYY");
        rb.setBid(new BigDecimal("5"));
        repo.save(rb);

        ReserveBidAudit a = new ReserveBidAudit();
        a.setReserveBidId(rb.getId());
        a.setOldPrice(new BigDecimal("5"));
        a.setNewPrice(new BigDecimal("6"));
        auditRepo.save(a);

        assertThat(auditRepo.findByReserveBidIdOrderByCreatedDateDesc(rb.getId())).hasSize(1);
    }
}
