package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.dto.BuyerCodeUpsertRequest;
import com.ecoatm.salesplatform.dto.BuyerUpsertRequest;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCodeChangeLog;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeChangeLogRepository;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Real-Postgres proof that {@link BuyerEditService#update} writes exactly one
 * {@code buyer_mgmt.buyer_code_change_logs} row when an admin actually changes a
 * buyer code's type, and none when the type is unchanged — the compliance audit
 * (legacy {@code SUB_LogBuyerCodeTypeChange_Compliance}). Runs under the
 * {@code pg-test} profile so Flyway applies V100 (the id sequence) end-to-end.
 *
 * <p>{@code @Transactional} rolls the seeded buyer/code + audit row back after
 * each test, per {@link PostgresIntegrationTest}.
 */
@Transactional
class BuyerCodeTypeChangeAuditIT extends PostgresIntegrationTest {

    @Autowired private BuyerEditService buyerEditService;
    @Autowired private BuyerRepository buyerRepository;
    @Autowired private BuyerCodeRepository buyerCodeRepository;
    @Autowired private BuyerCodeChangeLogRepository changeLogRepository;
    @Autowired private JdbcTemplate jdbc;

    private Long seedBuyerCode(String type) {
        LocalDateTime now = LocalDateTime.now();

        Buyer buyer = new Buyer();
        buyer.setCompanyName("IT Audit Co " + System.nanoTime());
        buyer.setStatus(BuyerStatus.Active);
        buyer.setSpecialBuyer(false);
        buyer.setCreatedDate(now);
        buyer.setChangedDate(now);
        Buyer savedBuyer = buyerRepository.saveAndFlush(buyer);

        BuyerCode code = new BuyerCode();
        code.setCode("ITAUD" + System.nanoTime());
        code.setBuyerCodeType(type);
        code.setBudget(5000);
        code.setStatus("Active");
        code.setSoftDelete(false);
        code.setCreatedDate(now);
        code.setChangedDate(now);
        BuyerCode savedCode = buyerCodeRepository.saveAndFlush(code);

        jdbc.update("INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES (?, ?)",
                savedCode.getId(), savedBuyer.getId());

        return savedBuyer.getId();
    }

    private Authentication adminAuth(long userId, String email) {
        return new UsernamePasswordAuthenticationToken(
                userId, email, List.of(new SimpleGrantedAuthority("ROLE_Administrator")));
    }

    private long anExistingUserId() {
        return jdbc.queryForObject("SELECT id FROM identity.users ORDER BY id LIMIT 1", Long.class);
    }

    @Test
    void adminTypeChange_writesAuditRow() {
        long userId = anExistingUserId();
        Long buyerId = seedBuyerCode("Wholesale");
        BuyerCode code = buyerCodeRepository.findByBuyerId(buyerId).get(0);

        BuyerCodeUpsertRequest codeReq = new BuyerCodeUpsertRequest(
                code.getId(), code.getCode(), "Data_Wipe", 5000, false);
        BuyerUpsertRequest req = new BuyerUpsertRequest(
                "IT Audit Co", null, false, null, List.of(codeReq));

        buyerEditService.update(buyerId, req, adminAuth(userId, "compliance-admin@test.com"));

        List<BuyerCodeChangeLog> logs = changeLogRepository.findByBuyerCodeIdOrderByIdDesc(code.getId());
        assertThat(logs).hasSize(1);
        BuyerCodeChangeLog log = logs.get(0);
        assertThat(log.getOldBuyerCodeType()).isEqualTo("Wholesale");
        assertThat(log.getNewBuyerCodeType()).isEqualTo("Data_Wipe");
        assertThat(log.getChangedById()).isEqualTo(userId);
        assertThat(log.getOwnerId()).isEqualTo(userId);
        assertThat(log.getEditedBy()).isEqualTo("compliance-admin@test.com");
        assertThat(log.getEditedOn()).isNotNull();
        assertThat(log.getCreatedDate()).isNotNull();

        // The change is really persisted on the buyer code too.
        assertThat(buyerCodeRepository.findById(code.getId()).orElseThrow().getBuyerCodeType())
                .isEqualTo("Data_Wipe");
    }

    @Test
    void adminSameType_writesNoAuditRow() {
        long userId = anExistingUserId();
        Long buyerId = seedBuyerCode("Wholesale");
        BuyerCode code = buyerCodeRepository.findByBuyerId(buyerId).get(0);

        // Same type, new budget — an edit that is not a type change.
        BuyerCodeUpsertRequest codeReq = new BuyerCodeUpsertRequest(
                code.getId(), code.getCode(), "Wholesale", 8000, false);
        BuyerUpsertRequest req = new BuyerUpsertRequest(
                "IT Audit Co", null, false, null, List.of(codeReq));

        buyerEditService.update(buyerId, req, adminAuth(userId, "compliance-admin@test.com"));

        assertThat(changeLogRepository.findByBuyerCodeIdOrderByIdDesc(code.getId())).isEmpty();
    }
}
