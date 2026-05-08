package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Test
    void searchAllNullParams_returnsPage() {
        Page<ReserveBid> p = repo.search(null, null, null, null, null, PageRequest.of(0, 5));
        assertThat(p.getTotalElements()).isGreaterThan(0);
        assertThat(p.getContent()).isNotEmpty();
    }

    @Test
    void searchByProductId_filtersToOne() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("99800");
        rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("12.34"));
        repo.save(rb);

        Page<ReserveBid> p = repo.search("99800", null, null, null, null, PageRequest.of(0, 5));
        assertThat(p.getContent()).hasSize(1);
        assertThat(p.getContent().get(0).getBid()).isEqualByComparingTo("12.34");
    }

    @Test
    void searchByBidRangeAndUpdatedSince_combinesPredicates() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("99801");
        rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("50.00"));
        rb.setLastUpdateDatetime(Instant.now());
        repo.save(rb);

        Page<ReserveBid> in = repo.search(
                null, null, new BigDecimal("40"), new BigDecimal("60"),
                Instant.now().minusSeconds(3600), PageRequest.of(0, 50));
        assertThat(in.getContent()).extracting(ReserveBid::getProductId).contains("99801");

        Page<ReserveBid> out = repo.search(
                null, null, new BigDecimal("60"), new BigDecimal("100"),
                null, PageRequest.of(0, 50));
        assertThat(out.getContent()).extracting(ReserveBid::getProductId).doesNotContain("99801");
    }
}
