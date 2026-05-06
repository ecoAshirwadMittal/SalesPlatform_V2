package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Entity-mapping smoke test for {@link AuctionsFeatureConfig}. Mirrors the
 * {@code @DataJpaTest} + {@code replace = NONE} pattern from
 * {@code BuyerCodeRepositoryIT} so Flyway-seeded data (V18 row id=1) is
 * actually present.
 *
 * <p>Sub-project 5's {@code R2BuyerAssignmentService} short-circuits to
 * {@code SKIPPED} when {@code calculate_round2_buyer_participation = false},
 * so the JPA mapping must round-trip the column for the singleton row.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AuctionsFeatureConfigIT {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("loads calculate_round2_buyer_participation as a boolean field")
    void loads_calculateRound2_field() {
        AuctionsFeatureConfig cfg = em.find(AuctionsFeatureConfig.class, 1L);
        assertThat(cfg).isNotNull();
        assertThat(cfg.isCalculateRound2BuyerParticipation())
                .as("V8 default is TRUE; V18 seed sets row id=1 to true")
                .isTrue();
    }
}
