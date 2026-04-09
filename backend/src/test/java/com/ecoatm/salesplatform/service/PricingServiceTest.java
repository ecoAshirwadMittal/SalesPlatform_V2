package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private PricingService pricingService;

    // --- Helpers ---

    private Device makeDevice(Long id, String sku, BigDecimal listPrice, BigDecimal futureListPrice,
                              BigDecimal minPrice, BigDecimal futureMinPrice) {
        Device d = new Device();
        d.setId(id);
        d.setSku(sku);
        d.setListPrice(listPrice);
        d.setFutureListPrice(futureListPrice);
        d.setMinPrice(minPrice);
        d.setFutureMinPrice(futureMinPrice);
        d.setIsActive(true);

        Brand brand = new Brand();
        brand.setDisplayName("Apple");
        d.setBrand(brand);

        Category category = new Category();
        category.setDisplayName("Cell Phone");
        d.setCategory(category);

        Model model = new Model();
        model.setDisplayName("iPhone 15");
        d.setModel(model);

        Carrier carrier = new Carrier();
        carrier.setDisplayName("Verizon");
        d.setCarrier(carrier);

        Capacity capacity = new Capacity();
        capacity.setDisplayName("256GB");
        d.setCapacity(capacity);

        Color color = new Color();
        color.setDisplayName("Black");
        d.setColor(color);

        Grade grade = new Grade();
        grade.setDisplayName("A");
        d.setGrade(grade);

        return d;
    }

    @Nested
    @DisplayName("listPricingDevices")
    class ListPricingDevices {

        @Test
        @DisplayName("returns paginated pricing device data with all price fields")
        void returnsPaginatedDataWithPriceFields() {
            Device device = makeDevice(1L, "PWS001", new BigDecimal("100.00"),
                    new BigDecimal("110.00"), new BigDecimal("80.00"), new BigDecimal("85.00"));

            Page<Device> page = new PageImpl<>(List.of(device), PageRequest.of(0, 20), 1);
            when(deviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null);

            assertThat(result.getContent()).hasSize(1);
            PricingDeviceResponse dto = result.getContent().get(0);
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getSku()).isEqualTo("PWS001");
            assertThat(dto.getCurrentListPrice()).isEqualByComparingTo("100.00");
            assertThat(dto.getFutureListPrice()).isEqualByComparingTo("110.00");
            assertThat(dto.getCurrentMinPrice()).isEqualByComparingTo("80.00");
            assertThat(dto.getFutureMinPrice()).isEqualByComparingTo("85.00");
            assertThat(dto.getBrandName()).isEqualTo("Apple");
            assertThat(dto.getCategoryName()).isEqualTo("Cell Phone");
            assertThat(dto.getModelName()).isEqualTo("iPhone 15");
        }

        @Test
        @DisplayName("returns empty page when no devices match")
        void returnsEmptyPageWhenNoDevices() {
            Page<Device> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
            when(deviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("handles null future prices gracefully")
        void handlesNullFuturePrices() {
            Device device = makeDevice(2L, "PWS002", new BigDecimal("50.00"),
                    null, new BigDecimal("40.00"), null);

            Page<Device> page = new PageImpl<>(List.of(device), PageRequest.of(0, 20), 1);
            when(deviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, null, null, null, null, null, null, null);

            PricingDeviceResponse dto = result.getContent().get(0);
            assertThat(dto.getFutureListPrice()).isNull();
            assertThat(dto.getFutureMinPrice()).isNull();
            assertThat(dto.getCurrentListPrice()).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("applies SKU filter as contains match")
        void appliesSkuFilter() {
            Device device = makeDevice(1L, "PWS001", BigDecimal.TEN, null, BigDecimal.ONE, null);
            Page<Device> page = new PageImpl<>(List.of(device), PageRequest.of(0, 20), 1);
            when(deviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), "PWS001", null, null, null, null, null, null, null);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("applies category filter as exact match")
        void appliesCategoryFilter() {
            Device device = makeDevice(1L, "PWS001", BigDecimal.TEN, null, BigDecimal.ONE, null);
            Page<Device> page = new PageImpl<>(List.of(device), PageRequest.of(0, 20), 1);
            when(deviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<PricingDeviceResponse> result = pricingService.listPricingDevices(
                    PageRequest.of(0, 20), null, "Cell Phone", null, null, null, null, null, null);

            assertThat(result.getContent()).hasSize(1);
        }
    }
}
