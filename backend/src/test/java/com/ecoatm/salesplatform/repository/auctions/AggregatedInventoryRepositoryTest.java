package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AggregatedInventoryRepositoryTest {

    @Autowired private AggregatedInventoryRepository repo;

    @Test
    @DisplayName("save and findById round-trips an AggregatedInventory row")
    void save_findById_roundTrip() {
        AggregatedInventory a = new AggregatedInventory();
        a.setEcoid2("999999");
        a.setName("Test Device");
        a.setBrand("TestBrand");
        a.setModel("TestModel");
        a.setCarrier("TestCarrier");
        a.setMergedGrade("A_YYY");
        a.setTotalQuantity(5);
        a.setDwTotalQuantity(2);
        a.setAvgTargetPrice(new java.math.BigDecimal("12.3400"));
        a.setChangedDate(java.time.Instant.now());

        AggregatedInventory saved = repo.save(a);
        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).get()
                .extracting(AggregatedInventory::getEcoid2).isEqualTo("999999");
    }
}
