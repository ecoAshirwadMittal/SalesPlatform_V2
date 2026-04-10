package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.mdm.Grade;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.mdm.GradeRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that validates the JPA Specification filter logic
 * used by PwsInventoryService.listFilteredDevices() against a real
 * H2 database. This catches SQL-level issues (NULL handling, JOINs)
 * that unit tests with mocked repositories cannot detect.
 */
@DataJpaTest
@ActiveProfiles("test")
class DeviceFilterSpecificationIT {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private GradeRepository gradeRepository;

    private Grade gradeA;
    private Grade gradeB;
    private Grade gradeAYYY;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
        gradeRepository.deleteAll();

        gradeA = saveGrade("A");
        gradeB = saveGrade("B");
        gradeAYYY = saveGrade("A_YYY");

        // Device 1: PWS, atpQty=10, grade=A (should appear in functional)
        saveDevice("PWS-GRADE-A", "PWS", 10, gradeA);

        // Device 2: PWS, atpQty=5, grade=B (should appear in functional)
        saveDevice("PWS-GRADE-B", "PWS", 5, gradeB);

        // Device 3: PWS, atpQty=3, grade=A_YYY (should be EXCLUDED from functional)
        saveDevice("PWS-GRADE-AYYY", "PWS", 3, gradeAYYY);

        // Device 4: PWS, atpQty=8, grade=NULL (should appear in functional)
        saveDevice("PWS-NO-GRADE", "PWS", 8, null);

        // Device 5: SPB, atpQty=10, grade=A (different itemType, excluded by itemType filter)
        saveDevice("SPB-GRADE-A", "SPB", 10, gradeA);

        // Device 6: PWS, atpQty=0, grade=A (excluded by minAtpQty > 0)
        saveDevice("PWS-ZERO-ATP", "PWS", 0, gradeA);

        // Device 7: PWS, atpQty=10, inactive (excluded by isActive filter)
        Device inactive = saveDevice("PWS-INACTIVE", "PWS", 10, gradeA);
        inactive.setIsActive(false);
        deviceRepository.save(inactive);
    }

    /** Replicates the Specification from PwsInventoryService.listFilteredDevices() */
    private Specification<Device> buildSpec(String itemType, String excludeGrade, Integer minAtpQty) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("isActive")));
            if (itemType != null) preds.add(cb.equal(root.get("itemType"), itemType));
            if (minAtpQty != null) preds.add(cb.greaterThan(root.get("atpQty"), minAtpQty));
            if (excludeGrade != null) {
                var gradeJoin = root.join("grade", JoinType.LEFT);
                preds.add(cb.or(
                        cb.isNull(gradeJoin),
                        cb.notEqual(gradeJoin.get("displayName"), excludeGrade)
                ));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
    }

    @Nested
    @DisplayName("excludeGrade filter")
    class ExcludeGradeFilter {

        @Test
        @DisplayName("includes devices with null grade when excluding A_YYY")
        void nullGrade_isIncluded() {
            Specification<Device> spec = buildSpec("PWS", "A_YYY", 0);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .contains("PWS-NO-GRADE");
        }

        @Test
        @DisplayName("excludes devices with A_YYY grade")
        void aYYYGrade_isExcluded() {
            Specification<Device> spec = buildSpec("PWS", "A_YYY", 0);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .doesNotContain("PWS-GRADE-AYYY");
        }

        @Test
        @DisplayName("includes devices with non-excluded grades (A, B)")
        void otherGrades_areIncluded() {
            Specification<Device> spec = buildSpec("PWS", "A_YYY", 0);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .contains("PWS-GRADE-A", "PWS-GRADE-B");
        }

        @Test
        @DisplayName("functional devices query returns correct set")
        void functionalDevicesQuery_returnsExpectedSet() {
            // This replicates the exact call from the inventory page:
            // ?itemType=PWS&minAtpQty=0&excludeGrade=A_YYY
            Specification<Device> spec = buildSpec("PWS", "A_YYY", 0);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .containsExactlyInAnyOrder("PWS-GRADE-A", "PWS-GRADE-B", "PWS-NO-GRADE")
                    .doesNotContain("PWS-GRADE-AYYY", "SPB-GRADE-A", "PWS-ZERO-ATP", "PWS-INACTIVE");
        }
    }

    @Nested
    @DisplayName("itemType filter")
    class ItemTypeFilter {

        @Test
        @DisplayName("filters by itemType=PWS")
        void filterByPWS() {
            Specification<Device> spec = buildSpec("PWS", null, null);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .allMatch(sku -> sku.startsWith("PWS-"))
                    .doesNotContain("SPB-GRADE-A");
        }
    }

    @Nested
    @DisplayName("minAtpQty filter")
    class MinAtpQtyFilter {

        @Test
        @DisplayName("excludes devices with atpQty <= minAtpQty")
        void excludesLowAtp() {
            Specification<Device> spec = buildSpec("PWS", null, 5);
            List<Device> results = deviceRepository.findAll(spec);

            assertThat(results).extracting(Device::getSku)
                    .contains("PWS-GRADE-A", "PWS-NO-GRADE")
                    .doesNotContain("PWS-GRADE-AYYY", "PWS-ZERO-ATP");
        }
    }

    @Nested
    @DisplayName("no filters")
    class NoFilters {

        @Test
        @DisplayName("returns all active devices when no filters applied")
        void returnsAllActive() {
            Specification<Device> spec = buildSpec(null, null, null);
            List<Device> results = deviceRepository.findAll(spec);

            // All active devices (6 active, 1 inactive)
            assertThat(results).hasSize(6)
                    .extracting(Device::getSku)
                    .doesNotContain("PWS-INACTIVE");
        }
    }

    // ── Helpers ──

    private Grade saveGrade(String name) {
        Grade g = new Grade();
        g.setName(name);
        g.setDisplayName(name);
        return gradeRepository.save(g);
    }

    private Device saveDevice(String sku, String itemType, int atpQty, Grade grade) {
        Device d = new Device();
        d.setSku(sku);
        d.setItemType(itemType);
        d.setAtpQty(atpQty);
        d.setGrade(grade);
        d.setIsActive(true);
        d.setListPrice(BigDecimal.TEN);
        return deviceRepository.save(d);
    }
}
