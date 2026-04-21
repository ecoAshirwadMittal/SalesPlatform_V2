package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Repository IT for {@link QualifiedBuyerCodeRepository}.
 * <p>
 * Mirrors the style of {@code WeekSyncWatermarkRepositoryTest} and
 * {@code AggregatedInventoryRepositoryTest}: {@code @DataJpaTest} against the
 * live local Postgres dev database with Flyway V1..V72 already applied. The
 * plan's suggested {@code @ActiveProfiles("test")} would swap in H2, which
 * cannot run the native-SQL delete under test — so we inherit the default
 * Postgres datasource instead.
 * <p>
 * Uses two real seeded {@code auctions.scheduling_auctions} ids and two real
 * {@code buyer_mgmt.buyer_codes} ids resolved at runtime so the FKs added in
 * V72 are satisfied. {@code @DataJpaTest} wraps each test in a
 * transaction that rolls back at the end, so nothing leaks into the dev DB.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class QualifiedBuyerCodeRepositoryIT {

    @Autowired private QualifiedBuyerCodeRepository repo;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("deleteBySchedulingAuctionId removes only rows for the target scheduling auction")
    void deleteBySchedulingAuctionId_removesOnlyTargetSaRows() {
        // Two distinct scheduling_auctions so we can prove the WHERE clause
        // is bounded, plus two distinct buyer_codes so the uq_qbc_sa_bc
        // unique constraint is never violated within the same SA.
        long[] saIds = firstTwoSchedulingAuctionIds();
        long[] bcIds = firstTwoBuyerCodeIds();
        long saTarget = saIds[0];
        long saOther  = saIds[1];
        long bc1      = bcIds[0];
        long bc2      = bcIds[1];

        QualifiedBuyerCode a = save(saTarget, bc1);
        QualifiedBuyerCode b = save(saTarget, bc2);
        QualifiedBuyerCode c = save(saOther,  bc1);
        // Force the INSERTs to hit the DB before the DELETE fires, otherwise
        // the native DELETE would run ahead of the pending persists.
        em.flush();

        int deleted = repo.deleteBySchedulingAuctionId(saTarget);
        // Evict the now-stale managed entities so findById re-reads from the DB
        // rather than returning cached copies the native DELETE bypassed.
        em.clear();

        assertThat(deleted).isEqualTo(2);
        assertThat(repo.findById(a.getId())).isEmpty();
        assertThat(repo.findById(b.getId())).isEmpty();
        assertThat(repo.findById(c.getId())).isPresent();
    }

    private QualifiedBuyerCode save(long saId, long bcId) {
        QualifiedBuyerCode qbc = new QualifiedBuyerCode();
        qbc.setSchedulingAuctionId(saId);
        qbc.setBuyerCodeId(bcId);
        qbc.setQualificationType(QualificationType.Qualified);
        qbc.setIncluded(true);
        return repo.save(qbc);
    }

    /**
     * Pull two distinct seeded {@code auctions.scheduling_auctions} ids so
     * inserts satisfy the V72 FK. V58 + dev seeds guarantee at least two rows.
     */
    private long[] firstTwoSchedulingAuctionIds() {
        @SuppressWarnings("unchecked")
        List<Number> ids = em.createNativeQuery(
                "SELECT id FROM auctions.scheduling_auctions ORDER BY id LIMIT 2")
                .getResultList();
        assertThat(ids).hasSize(2);
        return new long[] { ids.get(0).longValue(), ids.get(1).longValue() };
    }

    /**
     * Pull two distinct seeded {@code buyer_mgmt.buyer_codes} ids so inserts
     * satisfy the V72 FK. V18 seed provides hundreds of rows.
     */
    private long[] firstTwoBuyerCodeIds() {
        @SuppressWarnings("unchecked")
        List<Number> ids = em.createNativeQuery(
                "SELECT id FROM buyer_mgmt.buyer_codes ORDER BY id LIMIT 2")
                .getResultList();
        assertThat(ids).hasSize(2);
        return new long[] { ids.get(0).longValue(), ids.get(1).longValue() };
    }
}
