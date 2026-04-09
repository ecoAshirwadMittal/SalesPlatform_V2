package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for PricingService that exercises the actual JPA Specification
 * against an H2 database. This covers filter branches that unit tests with mocked
 * repositories cannot reach.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PricingServiceIT {

    @Autowired private PricingService pricingService;
    @Autowired private DeviceRepository deviceRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModelRepository modelRepository;
    @Autowired private CarrierRepository carrierRepository;
    @Autowired private CapacityRepository capacityRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private GradeRepository gradeRepository;

    private Brand apple, samsung;
    private Category cellPhone;
    private Model iphone15;
    private Carrier verizon, att;
    private Capacity cap256, cap128;
    private Color black, white;
    private Grade gradeA, gradeB;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();

        apple = saveBrand("Apple");
        samsung = saveBrand("Samsung");
        cellPhone = saveCategory("Cell Phone");
        iphone15 = saveModel("iPhone 15");
        verizon = saveCarrier("Verizon");
        att = saveCarrier("AT&T");
        cap256 = saveCapacity("256GB");
        cap128 = saveCapacity("128GB");
        black = saveColor("Black");
        white = saveColor("White");
        gradeA = saveGrade("A");
        gradeB = saveGrade("B");

        // Device 1: Apple, Verizon, 256GB, Black, A, listPrice=100, futureList=110, min=80, futureMin=85
        saveDevice("SKU-001", apple, cellPhone, iphone15, verizon, cap256, black, gradeA,
                bd("100.00"), bd("110.00"), bd("80.00"), bd("85.00"));

        // Device 2: Samsung, AT&T, 128GB, White, B, listPrice=50, no future
        saveDevice("SKU-002", samsung, cellPhone, iphone15, att, cap128, white, gradeB,
                bd("50.00"), null, bd("40.00"), null);

        // Device 3: Apple, Verizon, 256GB, Black, A, listPrice=200, futureList=220
        saveDevice("SKU-003", apple, cellPhone, iphone15, verizon, cap256, black, gradeA,
                bd("200.00"), bd("220.00"), bd("160.00"), bd("170.00"));
    }

    @Nested
    @DisplayName("listPricingDevices — integration")
    class ListPricingDevicesIT {

        @Test
        @DisplayName("returns all active devices when no filters")
        void noFilters() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("filters by SKU substring")
        void filterBySku() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), "001", null, null, null, null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("filters by brand")
        void filterByBrand() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, "Apple", null, null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("filters by category")
        void filterByCategory() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, "Cell Phone", null, null, null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("filters by model")
        void filterByModel() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, "iPhone 15", null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("filters by carrier")
        void filterByCarrier() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, "AT&T", null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-002");
        }

        @Test
        @DisplayName("filters by capacity")
        void filterByCapacity() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, "128GB", null, null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("filters by color")
        void filterByColor() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, "White", null,
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("filters by grade")
        void filterByGrade() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, "B",
                    null, null, null, null);
            assertThat(page.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("filters by currentListPrice equality")
        void filterByCurrentListPrice() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null,
                    bd("100.00"), null, null, null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("filters by futureListPrice equality")
        void filterByFutureListPrice() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null,
                    null, bd("220.00"), null, null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-003");
        }

        @Test
        @DisplayName("filters by currentMinPrice equality")
        void filterByCurrentMinPrice() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null,
                    null, null, bd("40.00"), null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-002");
        }

        @Test
        @DisplayName("filters by futureMinPrice equality")
        void filterByFutureMinPrice() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null,
                    null, null, null, bd("170.00"));
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-003");
        }

        @Test
        @DisplayName("combined brand + price filter")
        void combinedBrandAndPrice() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, "Apple", null, null, null, null, null,
                    bd("100.00"), null, null, null);
            assertThat(page.getContent()).hasSize(1)
                    .extracting(PricingDeviceResponse::getSku).containsExactly("SKU-001");
        }

        @Test
        @DisplayName("non-matching filter returns empty")
        void nonMatchingFilter() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, "Nokia", null, null, null, null, null,
                    null, null, null, null);
            assertThat(page.getContent()).isEmpty();
        }

        @Test
        @DisplayName("DTO maps lookup names correctly")
        void dtoMapsLookupNames() {
            Page<PricingDeviceResponse> page = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), "SKU-001", null, null, null, null, null, null, null,
                    null, null, null, null);
            PricingDeviceResponse dto = page.getContent().get(0);
            assertThat(dto.getBrandName()).isEqualTo("Apple");
            assertThat(dto.getCategoryName()).isEqualTo("Cell Phone");
            assertThat(dto.getModelName()).isEqualTo("iPhone 15");
            assertThat(dto.getCarrierName()).isEqualTo("Verizon");
            assertThat(dto.getCapacityName()).isEqualTo("256GB");
            assertThat(dto.getColorName()).isEqualTo("Black");
            assertThat(dto.getGradeName()).isEqualTo("A");
        }
    }

    @Nested
    @DisplayName("updateFuturePrices — integration")
    class UpdateFuturePricesIT {

        @Test
        @DisplayName("updates and persists future prices")
        void updatesAndPersists() {
            Long id = deviceRepository.findBySku("SKU-001").get().getId();
            PricingDeviceResponse result = pricingService.updateFuturePrices(
                    id, bd("130.00"), bd("105.00"));
            assertThat(result.getFutureListPrice()).isEqualByComparingTo("130.00");
            assertThat(result.getFutureMinPrice()).isEqualByComparingTo("105.00");
        }

        @Test
        @DisplayName("throws when device not found")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> pricingService.updateFuturePrices(
                    999999L, bd("10.00"), bd("5.00")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Helpers ──

    private static BigDecimal bd(String v) { return new BigDecimal(v); }

    private Brand saveBrand(String name) {
        Brand b = new Brand(); b.setName(name); b.setDisplayName(name);
        return brandRepository.save(b);
    }
    private Category saveCategory(String name) {
        Category c = new Category(); c.setName(name); c.setDisplayName(name);
        return categoryRepository.save(c);
    }
    private Model saveModel(String name) {
        Model m = new Model(); m.setName(name); m.setDisplayName(name);
        return modelRepository.save(m);
    }
    private Carrier saveCarrier(String name) {
        Carrier c = new Carrier(); c.setName(name); c.setDisplayName(name);
        return carrierRepository.save(c);
    }
    private Capacity saveCapacity(String name) {
        Capacity c = new Capacity(); c.setName(name); c.setDisplayName(name);
        return capacityRepository.save(c);
    }
    private Color saveColor(String name) {
        Color c = new Color(); c.setName(name); c.setDisplayName(name);
        return colorRepository.save(c);
    }
    private Grade saveGrade(String name) {
        Grade g = new Grade(); g.setName(name); g.setDisplayName(name);
        return gradeRepository.save(g);
    }
    private Device saveDevice(String sku, Brand brand, Category cat, Model model,
            Carrier carrier, Capacity capacity, Color color, Grade grade,
            BigDecimal listPrice, BigDecimal futureListPrice,
            BigDecimal minPrice, BigDecimal futureMinPrice) {
        Device d = new Device();
        d.setSku(sku); d.setIsActive(true);
        d.setBrand(brand); d.setCategory(cat); d.setModel(model);
        d.setCarrier(carrier); d.setCapacity(capacity);
        d.setColor(color); d.setGrade(grade);
        d.setListPrice(listPrice); d.setFutureListPrice(futureListPrice);
        d.setMinPrice(minPrice); d.setFutureMinPrice(futureMinPrice);
        return deviceRepository.save(d);
    }
}
