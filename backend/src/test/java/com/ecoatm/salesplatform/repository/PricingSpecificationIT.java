package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that validates PricingService's Specification filter logic
 * against a real H2 database. Covers all filter branches including lookup joins
 * and price equality filters.
 */
@DataJpaTest
@ActiveProfiles("test")
class PricingSpecificationIT {

    @Autowired private DeviceRepository deviceRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModelRepository modelRepository;
    @Autowired private CarrierRepository carrierRepository;
    @Autowired private CapacityRepository capacityRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private GradeRepository gradeRepository;

    private Brand apple;
    private Brand samsung;
    private Category cellPhone;
    private Model iphone15;
    private Carrier verizon;
    private Capacity cap256;
    private Color black;
    private Grade gradeA;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();

        apple = saveBrand("Apple");
        samsung = saveBrand("Samsung");
        cellPhone = saveCategory("Cell Phone");
        iphone15 = saveModel("iPhone 15");
        verizon = saveCarrier("Verizon");
        cap256 = saveCapacity("256GB");
        black = saveColor("Black");
        gradeA = saveGrade("A");

        // Device 1: full lookups, listPrice=100, futureListPrice=110, minPrice=80, futureMinPrice=85
        saveDevice("SKU-001", apple, cellPhone, iphone15, verizon, cap256, black, gradeA,
                new BigDecimal("100.00"), new BigDecimal("110.00"),
                new BigDecimal("80.00"), new BigDecimal("85.00"));

        // Device 2: Samsung, listPrice=50, no future prices
        saveDevice("SKU-002", samsung, cellPhone, iphone15, verizon, cap256, black, gradeA,
                new BigDecimal("50.00"), null,
                new BigDecimal("40.00"), null);

        // Device 3: Apple, different listPrice
        saveDevice("SKU-003", apple, cellPhone, iphone15, verizon, cap256, black, gradeA,
                new BigDecimal("200.00"), new BigDecimal("220.00"),
                new BigDecimal("160.00"), new BigDecimal("170.00"));
    }

    /** Replicates the Specification from PricingService.listPricingDevices() */
    private Specification<Device> buildSpec(String sku, String category, String brand,
            String model, String carrier, String capacity, String color, String grade,
            BigDecimal currentListPrice, BigDecimal futureListPrice,
            BigDecimal currentMinPrice, BigDecimal futureMinPrice) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("isActive")));

            if (sku != null && !sku.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                var join = root.join("category", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), category));
            }
            if (brand != null && !brand.isBlank()) {
                var join = root.join("brand", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), brand));
            }
            if (model != null && !model.isBlank()) {
                var join = root.join("model", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), model));
            }
            if (carrier != null && !carrier.isBlank()) {
                var join = root.join("carrier", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), carrier));
            }
            if (capacity != null && !capacity.isBlank()) {
                var join = root.join("capacity", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), capacity));
            }
            if (color != null && !color.isBlank()) {
                var join = root.join("color", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), color));
            }
            if (grade != null && !grade.isBlank()) {
                var join = root.join("grade", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), grade));
            }
            if (currentListPrice != null) {
                preds.add(cb.equal(root.get("listPrice"), currentListPrice));
            }
            if (futureListPrice != null) {
                preds.add(cb.equal(root.get("futureListPrice"), futureListPrice));
            }
            if (currentMinPrice != null) {
                preds.add(cb.equal(root.get("minPrice"), currentMinPrice));
            }
            if (futureMinPrice != null) {
                preds.add(cb.equal(root.get("futureMinPrice"), futureMinPrice));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }

    @Nested
    @DisplayName("no filters")
    class NoFilters {
        @Test
        @DisplayName("returns all active devices")
        void returnsAll() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }
    }

    @Nested
    @DisplayName("SKU filter")
    class SkuFilter {
        @Test
        @DisplayName("filters by partial SKU match")
        void partialMatch() {
            Specification<Device> spec = buildSpec(
                    "001", null, null, null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-001");
        }
    }

    @Nested
    @DisplayName("lookup join filters")
    class LookupFilters {
        @Test
        @DisplayName("filters by brand name")
        void filterByBrand() {
            Specification<Device> spec = buildSpec(
                    null, null, "Apple", null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(2)
                    .extracting(Device::getSku).containsExactlyInAnyOrder("SKU-001", "SKU-003");
        }

        @Test
        @DisplayName("filters by category")
        void filterByCategory() {
            Specification<Device> spec = buildSpec(
                    null, "Cell Phone", null, null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("filters by model")
        void filterByModel() {
            Specification<Device> spec = buildSpec(
                    null, null, null, "iPhone 15", null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("filters by carrier")
        void filterByCarrier() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, "Verizon", null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("filters by capacity")
        void filterByCapacity() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, "256GB", null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("filters by color")
        void filterByColor() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, "Black", null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("filters by grade")
        void filterByGrade() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, "A",
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("non-matching brand returns empty")
        void nonMatchingBrand() {
            Specification<Device> spec = buildSpec(
                    null, null, "Nokia", null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("price equality filters")
    class PriceFilters {
        @Test
        @DisplayName("filters by currentListPrice")
        void filterByCurrentListPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    new BigDecimal("100.00"), null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("filters by futureListPrice")
        void filterByFutureListPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    null, new BigDecimal("110.00"), null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("filters by currentMinPrice")
        void filterByCurrentMinPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    null, null, new BigDecimal("40.00"), null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-002");
        }

        @Test
        @DisplayName("filters by futureMinPrice")
        void filterByFutureMinPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    null, null, null, new BigDecimal("170.00"));
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-003");
        }

        @Test
        @DisplayName("non-matching price returns empty")
        void nonMatchingPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    new BigDecimal("999.99"), null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("combined filters")
    class CombinedFilters {
        @Test
        @DisplayName("brand + price narrows results correctly")
        void brandAndPrice() {
            Specification<Device> spec = buildSpec(
                    null, null, "Apple", null, null, null, null, null,
                    new BigDecimal("100.00"), null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("SKU + brand filters combined")
        void skuAndBrand() {
            Specification<Device> spec = buildSpec(
                    "SKU", null, "Samsung", null, null, null, null, null,
                    null, null, null, null);
            List<Device> results = deviceRepository.findAll(spec);
            assertThat(results).hasSize(1)
                    .extracting(Device::getSku).containsExactly("SKU-002");
        }
    }

    @Nested
    @DisplayName("pagination")
    class Pagination {
        @Test
        @DisplayName("returns correct page with pagination")
        void paginates() {
            Specification<Device> spec = buildSpec(
                    null, null, null, null, null, null, null, null,
                    null, null, null, null);
            Page<Device> page = deviceRepository.findAll(spec, PageRequest.of(0, 2));
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(3);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }
    }

    // ── Helpers ──

    private Brand saveBrand(String name) {
        Brand b = new Brand();
        b.setName(name);
        b.setDisplayName(name);
        return brandRepository.save(b);
    }

    private Category saveCategory(String name) {
        Category c = new Category();
        c.setName(name);
        c.setDisplayName(name);
        return categoryRepository.save(c);
    }

    private Model saveModel(String name) {
        Model m = new Model();
        m.setName(name);
        m.setDisplayName(name);
        return modelRepository.save(m);
    }

    private Carrier saveCarrier(String name) {
        Carrier c = new Carrier();
        c.setName(name);
        c.setDisplayName(name);
        return carrierRepository.save(c);
    }

    private Capacity saveCapacity(String name) {
        Capacity c = new Capacity();
        c.setName(name);
        c.setDisplayName(name);
        return capacityRepository.save(c);
    }

    private Color saveColor(String name) {
        Color c = new Color();
        c.setName(name);
        c.setDisplayName(name);
        return colorRepository.save(c);
    }

    private Grade saveGrade(String name) {
        Grade g = new Grade();
        g.setName(name);
        g.setDisplayName(name);
        return gradeRepository.save(g);
    }

    private Device saveDevice(String sku, Brand brand, Category category, Model model,
            Carrier carrier, Capacity capacity, Color color, Grade grade,
            BigDecimal listPrice, BigDecimal futureListPrice,
            BigDecimal minPrice, BigDecimal futureMinPrice) {
        Device d = new Device();
        d.setSku(sku);
        d.setIsActive(true);
        d.setBrand(brand);
        d.setCategory(category);
        d.setModel(model);
        d.setCarrier(carrier);
        d.setCapacity(capacity);
        d.setColor(color);
        d.setGrade(grade);
        d.setListPrice(listPrice);
        d.setFutureListPrice(futureListPrice);
        d.setMinPrice(minPrice);
        d.setFutureMinPrice(futureMinPrice);
        return deviceRepository.save(d);
    }
}
