package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Repository IT for {@link BuyerCodeRepository#findActiveWholesaleOrDataWipe()}.
 * <p>
 * Uses the same {@code @DataJpaTest} + {@code replace = NONE} pattern as
 * {@link QualifiedBuyerCodeRepositoryIT} — drives the native query against
 * the live dev Postgres seed so the join across {@code buyer_codes},
 * {@code buyer_code_buyers}, and {@code buyers} is exercised for real.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class BuyerCodeRepositoryIT {

    @Autowired
    BuyerCodeRepository buyerCodeRepo;

    @Test
    @DisplayName("findActiveWholesaleOrDataWipe returns only active Wholesale or Data_Wipe buyer codes")
    void findActiveWholesaleOrDataWipe_returnsOnlyActiveWholesaleAndDataWipeBuyerCodes() {
        List<BuyerCode> codes = buyerCodeRepo.findActiveWholesaleOrDataWipe();

        assertThat(codes).allSatisfy(bc -> {
            assertThat(bc.getBuyerCodeType()).isIn("Wholesale", "Data_Wipe");
            assertThat(bc.isSoftDelete()).isFalse();
        });
    }
}
