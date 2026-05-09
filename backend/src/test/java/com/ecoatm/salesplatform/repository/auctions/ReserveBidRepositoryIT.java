package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterColumn;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterOp;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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

    // ── searchDynamic — empty-filter baseline ─────────────────────────

    @Test
    void searchDynamic_noFilters_returnsPage() {
        Page<ReserveBid> p = repo.searchDynamic(List.of(), PageRequest.of(0, 5));
        assertThat(p.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void searchDynamic_unsorted_returnsPaged() {
        Page<ReserveBid> p = repo.searchDynamic(List.of(), PageRequest.of(0, 3));
        assertThat(p.getContent()).hasSizeLessThanOrEqualTo(3);
    }

    // ── searchDynamic — text ops on grade ─────────────────────────────

    @Test
    void searchDynamic_contains_textOp() {
        seedSimpleRow("88001", "A_UNIQUEMATCH", new BigDecimal("1.00"));
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.GRADE, FilterOp.CONTAINS, "uniquematch")),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88001");
    }

    @Test
    void searchDynamic_startsWith_textOp() {
        seedSimpleRow("88002", "ZSTART_PREFIX_X", new BigDecimal("1.00"));
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.GRADE, FilterOp.STARTS_WITH, "zstart_prefix")),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88002");
    }

    @Test
    void searchDynamic_endsWith_textOp() {
        seedSimpleRow("88003", "X_SUFFIX_ZEND", new BigDecimal("1.00"));
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.GRADE, FilterOp.ENDS_WITH, "_ZEND")),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88003");
    }

    // ── searchDynamic — numeric ops on bid ────────────────────────────

    @Test
    void searchDynamic_eq_numericOp() {
        seedSimpleRow("88010", "A_YYY", new BigDecimal("123.45"));
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88010"),
                        new FilterSpec(FilterColumn.BID, FilterOp.EQ, "123.45")),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88010");
    }

    @Test
    void searchDynamic_gt_lt_numericRange() {
        seedSimpleRow("88011", "A_YYY", new BigDecimal("50.00"));
        Page<ReserveBid> in = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88011"),
                        new FilterSpec(FilterColumn.BID, FilterOp.GT, "40"),
                        new FilterSpec(FilterColumn.BID, FilterOp.LT, "60")),
                PageRequest.of(0, 5));
        assertThat(in.getContent()).extracting(ReserveBid::getProductId).contains("88011");

        Page<ReserveBid> out = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88011"),
                        new FilterSpec(FilterColumn.BID, FilterOp.GT, "60")),
                PageRequest.of(0, 5));
        assertThat(out.getContent()).extracting(ReserveBid::getProductId).doesNotContain("88011");
    }

    @Test
    void searchDynamic_neq_numericOp() {
        seedSimpleRow("88012", "A_YYY", new BigDecimal("777.77"));
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88012"),
                        new FilterSpec(FilterColumn.BID, FilterOp.NEQ, "1.00")),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88012");
    }

    // ── searchDynamic — empty / not-empty ─────────────────────────────

    @Test
    void searchDynamic_empty_textColumn() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("88020");
        rb.setGrade("A_YYY");
        rb.setBrand(null);
        rb.setBid(new BigDecimal("1.00"));
        repo.save(rb);

        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88020"),
                        new FilterSpec(FilterColumn.BRAND, FilterOp.EMPTY, null)),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88020");
    }

    @Test
    void searchDynamic_notEmpty_textColumn() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("88021");
        rb.setGrade("A_YYY");
        rb.setBrand("HasBrand");
        rb.setBid(new BigDecimal("1.00"));
        repo.save(rb);

        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88021"),
                        new FilterSpec(FilterColumn.BRAND, FilterOp.NOT_EMPTY, null)),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88021");
    }

    // ── searchDynamic — date ops ──────────────────────────────────────

    @Test
    void searchDynamic_gte_date() {
        ReserveBid rb = new ReserveBid();
        rb.setProductId("88030");
        rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("1.00"));
        rb.setLastUpdateDatetime(Instant.now());
        repo.save(rb);

        String yesterday = Instant.now().minusSeconds(86_400).toString();
        Page<ReserveBid> p = repo.searchDynamic(
                List.of(new FilterSpec(FilterColumn.PRODUCT_ID, FilterOp.EQ, "88030"),
                        new FilterSpec(FilterColumn.LAST_UPDATE_DATETIME, FilterOp.GTE, yesterday)),
                PageRequest.of(0, 5));
        assertThat(p.getContent()).extracting(ReserveBid::getProductId).contains("88030");
    }

    // ── Sort regression ───────────────────────────────────────────────

    @Test
    void searchDynamic_sortByBid_appliesOrderBy() {
        Page<ReserveBid> asc = repo.searchDynamic(List.of(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "bid")));
        Page<ReserveBid> desc = repo.searchDynamic(List.of(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "bid")));
        assertThat(asc.getContent()).isNotEmpty();
        assertThat(desc.getContent()).isNotEmpty();
        assertThat(asc.getContent().get(0).getBid())
                .isLessThanOrEqualTo(desc.getContent().get(0).getBid());
    }

    private void seedSimpleRow(String productId, String grade, BigDecimal bid) {
        ReserveBid rb = new ReserveBid();
        rb.setProductId(productId);
        rb.setGrade(grade);
        rb.setBid(bid);
        repo.save(rb);
    }
}
