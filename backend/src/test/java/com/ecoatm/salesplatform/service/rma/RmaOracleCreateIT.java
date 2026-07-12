package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link RmaOracleService#createRmaInOracle} against a real
 * PostgreSQL database — proves the {@code oracle_*} column write path persists
 * end-to-end on the real schema (column names + types), not just against mocked
 * repositories.
 *
 * <p>Runs under the {@code dev} profile (in addition to {@code pg-test}) so the
 * Oracle toggle-off path {@link com.ecoatm.salesplatform.service.OracleOrderClient
 * simulates success} (a {@code SIM-} number) rather than failing closed — this
 * exercises the success branch that records {@code oracle_number} +
 * {@code is_successful=true}, matching the brief's "SIM values in dev".
 *
 * <p><b>Not {@code @Transactional}.</b> {@code createRmaInOracle} is
 * {@code @Transactional(REQUIRES_NEW)}, so it commits in its own transaction; a
 * test-managed transaction would (a) hide the committed seed from that new
 * transaction and (b) not roll back its writes anyway. The seed is therefore
 * committed via {@link TransactionTemplate} and torn down explicitly in
 * {@link #cleanup()} (also run before seeding, to clear any row a crashed run
 * left behind). High, test-only IDs avoid collision with migrated data.
 */
@ActiveProfiles(value = {"pg-test", "dev"}, inheritProfiles = false)
class RmaOracleCreateIT extends PostgresIntegrationTest {

    private static final long BUYER_CODE_ID = 90101L;
    private static final long DEVICE_ID = 90101L;
    private static final long RMA_ID = 90101L;
    private static final long RMA_ITEM_ID = 90101L;

    @Autowired private RmaOracleService rmaOracleService;
    @Autowired private RmaRepository rmaRepository;
    @Autowired private TransactionTemplate txTemplate;
    @Autowired private EntityManager em;

    @BeforeEach
    void seed() {
        cleanup();
        txTemplate.executeWithoutResult(s -> {
            em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyers (id, company_name, status)
                    VALUES (:id, 'RMA Oracle IT Corp', 'Active') ON CONFLICT (id) DO NOTHING
                    """).setParameter("id", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status, soft_delete)
                    VALUES (:id, 'IT-ORCL-BC', 'Wholesale', 'Active', false) ON CONFLICT (id) DO NOTHING
                    """).setParameter("id", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                    VALUES (:id, :id) ON CONFLICT DO NOTHING
                    """).setParameter("id", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("""
                    INSERT INTO mdm.device (id, sku, is_active, available_qty, atp_qty, list_price, min_price)
                    VALUES (:id, 'IT-ORCL-SKU', true, 100, 100, 200.00, 100.00) ON CONFLICT (id) DO NOTHING
                    """).setParameter("id", DEVICE_ID).executeUpdate();
            // Approved RMA, reviewer null (→ payload originSystemUser 'system'),
            // rma_status null (createRmaInOracle does not touch status).
            em.createNativeQuery("""
                    INSERT INTO pws.rma (id, number, buyer_code_id, system_status, created_date, updated_date)
                    VALUES (:id, 'RMA-ORCL-IT-1', :bc, 'Approved', NOW(), NOW()) ON CONFLICT (id) DO NOTHING
                    """).setParameter("id", RMA_ID).setParameter("bc", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("""
                    INSERT INTO pws.rma_item (id, rma_id, device_id, imei, order_number, sale_price, return_reason, status, created_date, updated_date)
                    VALUES (:id, :rma, :device, 'ORCL-IMEI-1', 'ORD-ORCL-1', 175, 'Physically Damaged', 'Approve', NOW(), NOW()) ON CONFLICT (id) DO NOTHING
                    """).setParameter("id", RMA_ITEM_ID).setParameter("rma", RMA_ID)
                    .setParameter("device", DEVICE_ID).executeUpdate();
        });
    }

    @AfterEach
    void cleanup() {
        txTemplate.executeWithoutResult(s -> {
            // rma delete cascades rma_item (FK ON DELETE CASCADE); order matters
            // for the device / buyer_code FKs.
            em.createNativeQuery("DELETE FROM pws.rma WHERE id = :id")
                    .setParameter("id", RMA_ID).executeUpdate();
            em.createNativeQuery("DELETE FROM mdm.device WHERE id = :id")
                    .setParameter("id", DEVICE_ID).executeUpdate();
            em.createNativeQuery("DELETE FROM buyer_mgmt.buyer_code_buyers WHERE buyer_code_id = :id")
                    .setParameter("id", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("DELETE FROM buyer_mgmt.buyer_codes WHERE id = :id")
                    .setParameter("id", BUYER_CODE_ID).executeUpdate();
            em.createNativeQuery("DELETE FROM buyer_mgmt.buyers WHERE id = :id")
                    .setParameter("id", BUYER_CODE_ID).executeUpdate();
        });
    }

    @Test
    @DisplayName("createRmaInOracle persists oracle_* columns (SIM success) + json_content on real Postgres")
    void createRmaInOracle_persistsOracleColumns() {
        rmaOracleService.createRmaInOracle(RMA_ID);

        Rma rma = txTemplate.execute(s -> rmaRepository.findById(RMA_ID).orElseThrow());

        // SIM success branch (dev profile, Oracle toggle off / no config row).
        assertThat(rma.getIsSuccessful()).isTrue();
        assertThat(rma.getOracleNumber()).isNotNull().startsWith("SIM-");
        assertThat(rma.getOracleRmaStatus()).contains("simulated success");
        // SIM response carries no orderId / httpCode.
        assertThat(rma.getOracleId()).isNull();
        assertThat(rma.getOracleHttpCode()).isNull();

        // The built payload was captured, includes the RMA number, the PWS-RMA
        // order type, and the approved line's resolved SKU.
        assertThat(rma.getJsonContent())
                .isNotNull()
                .contains("RMA-ORCL-IT-1")
                .contains("PWS-RMA")
                .contains("IT-ORCL-SKU");
    }
}
