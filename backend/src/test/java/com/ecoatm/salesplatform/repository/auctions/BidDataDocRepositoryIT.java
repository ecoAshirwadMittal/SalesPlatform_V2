package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("pg-test")  // SWAPPED from "test" — see scene-setting context
class BidDataDocRepositoryIT {

    @Autowired
    private BidDataDocRepository repo;

    @Test
    void findByUserIdAndBuyerCodeIdAndWeekId_returnsDoc_whenPresent() {
        BidDataDoc doc = new BidDataDoc();
        doc.setUserId(1L);
        doc.setBuyerCodeId(2L);
        doc.setWeekId(3L);
        doc.setCreatedDate(Instant.now());
        doc.setChangedDate(Instant.now());
        repo.save(doc);

        Optional<BidDataDoc> found =
            repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(doc.getId());
    }

    @Test
    void findByUserIdAndBuyerCodeIdAndWeekId_empty_whenAbsent() {
        assertThat(repo.findByUserIdAndBuyerCodeIdAndWeekId(999L, 999L, 999L))
            .isEmpty();
    }
}
