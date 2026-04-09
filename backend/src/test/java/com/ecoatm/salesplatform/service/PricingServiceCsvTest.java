package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.CsvUploadResult;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceCsvTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private PricingService pricingService;

    private InputStream csv(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private Device makeDevice(String sku) {
        Device d = new Device();
        d.setId(1L);
        d.setSku(sku);
        d.setIsActive(true);
        d.setListPrice(new BigDecimal("100.00"));
        d.setMinPrice(new BigDecimal("80.00"));
        return d;
    }

    @Nested
    @DisplayName("processPricingCsv")
    class ProcessPricingCsv {

        @Test
        @DisplayName("parses valid CSV and updates devices")
        void parsesValidCsv() {
            String content = "sku,futureListPrice,futureMinPrice\nPWS001,120.00,95.00\nPWS002,60.00,45.00\n";
            Device d1 = makeDevice("PWS001");
            Device d2 = makeDevice("PWS002");
            d2.setId(2L);
            when(deviceRepository.findBySku("PWS001")).thenReturn(Optional.of(d1));
            when(deviceRepository.findBySku("PWS002")).thenReturn(Optional.of(d2));
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.totalRows()).isEqualTo(2);
            assertThat(result.updatedCount()).isEqualTo(2);
            assertThat(result.errorCount()).isZero();
            assertThat(result.errors()).isEmpty();
            verify(deviceRepository, times(2)).save(any(Device.class));
        }

        @Test
        @DisplayName("reports error for unknown SKU")
        void reportsUnknownSku() {
            String content = "sku,futureListPrice,futureMinPrice\nBADSKU,120.00,95.00\n";
            when(deviceRepository.findBySku("BADSKU")).thenReturn(Optional.empty());

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.totalRows()).isEqualTo(1);
            assertThat(result.updatedCount()).isZero();
            assertThat(result.errorCount()).isEqualTo(1);
            assertThat(result.errors().get(0)).contains("BADSKU");
        }

        @Test
        @DisplayName("reports error for invalid decimal values")
        void reportsInvalidDecimal() {
            String content = "sku,futureListPrice,futureMinPrice\nPWS001,abc,95.00\n";

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.errorCount()).isEqualTo(1);
            assertThat(result.errors().get(0)).contains("Row 2");
        }

        @Test
        @DisplayName("reports error when row has wrong column count")
        void reportsWrongColumnCount() {
            String content = "sku,futureListPrice,futureMinPrice\nPWS001,120.00\n";

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.errorCount()).isEqualTo(1);
            assertThat(result.errors().get(0)).contains("Row 2");
        }

        @Test
        @DisplayName("handles empty CSV with only header")
        void handlesEmptyCsv() {
            String content = "sku,futureListPrice,futureMinPrice\n";

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.totalRows()).isZero();
            assertThat(result.updatedCount()).isZero();
            assertThat(result.errorCount()).isZero();
        }

        @Test
        @DisplayName("partial success - valid rows update, invalid rows reported")
        void partialSuccess() {
            String content = "sku,futureListPrice,futureMinPrice\nPWS001,120.00,95.00\nBADSKU,60.00,45.00\n";
            Device d1 = makeDevice("PWS001");
            when(deviceRepository.findBySku("PWS001")).thenReturn(Optional.of(d1));
            when(deviceRepository.findBySku("BADSKU")).thenReturn(Optional.empty());
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.totalRows()).isEqualTo(2);
            assertThat(result.updatedCount()).isEqualTo(1);
            assertThat(result.errorCount()).isEqualTo(1);
            verify(deviceRepository, times(1)).save(any(Device.class));
        }

        @Test
        @DisplayName("rejects SKU with CSV injection characters")
        void rejectsCsvInjectionSku() {
            String content = "sku,futureListPrice,futureMinPrice\n=CMD|'/C calc'!A0,120.00,95.00\n";

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.errorCount()).isEqualTo(1);
            assertThat(result.errors().get(0)).contains("disallowed leading character");
        }

        @Test
        @DisplayName("reports error when futureListPrice < futureMinPrice")
        void reportsListPriceLessThanMinPrice() {
            String content = "sku,futureListPrice,futureMinPrice\nPWS001,50.00,95.00\n";

            CsvUploadResult result = pricingService.processPricingCsv(csv(content));

            assertThat(result.errorCount()).isEqualTo(1);
            assertThat(result.errors().get(0)).contains("less than");
        }
    }
}
