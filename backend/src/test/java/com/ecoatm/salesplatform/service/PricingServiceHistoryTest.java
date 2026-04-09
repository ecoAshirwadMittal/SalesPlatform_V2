package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.PriceHistoryResponse;
import com.ecoatm.salesplatform.model.mdm.PriceHistory;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.mdm.PriceHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceHistoryTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @InjectMocks
    private PricingService pricingService;

    private PriceHistory makeHistory(Long id, Long deviceId, BigDecimal listPrice, BigDecimal minPrice, LocalDateTime created) {
        PriceHistory h = new PriceHistory();
        h.setId(id);
        h.setDeviceId(deviceId);
        h.setListPrice(listPrice);
        h.setMinPrice(minPrice);
        h.setCreatedDate(created);
        return h;
    }

    @Nested
    @DisplayName("getPriceHistory")
    class GetPriceHistory {

        @Test
        @DisplayName("returns history entries with derived previous prices")
        void returnsHistoryWithDerivedPreviousPrices() {
            LocalDateTime now = LocalDateTime.of(2026, 4, 8, 12, 0);
            PriceHistory h1 = makeHistory(1L, 100L, new BigDecimal("120.00"), new BigDecimal("95.00"), now);
            PriceHistory h2 = makeHistory(2L, 100L, new BigDecimal("100.00"), new BigDecimal("80.00"), now.minusDays(30));
            PriceHistory h3 = makeHistory(3L, 100L, new BigDecimal("90.00"), new BigDecimal("70.00"), now.minusDays(60));

            when(priceHistoryRepository.findByDeviceIdOrderByCreatedDateDesc(100L))
                    .thenReturn(List.of(h1, h2, h3));

            List<PriceHistoryResponse> result = pricingService.getPriceHistory(100L);

            assertThat(result).hasSize(3);
            // Most recent entry: previous = h2's prices
            assertThat(result.get(0).listPrice()).isEqualByComparingTo("120.00");
            assertThat(result.get(0).previousListPrice()).isEqualByComparingTo("100.00");
            assertThat(result.get(0).previousMinPrice()).isEqualByComparingTo("80.00");
            // Middle entry: previous = h3's prices
            assertThat(result.get(1).previousListPrice()).isEqualByComparingTo("90.00");
            // Oldest entry: no previous
            assertThat(result.get(2).previousListPrice()).isNull();
            assertThat(result.get(2).previousMinPrice()).isNull();
        }

        @Test
        @DisplayName("returns empty list when no history exists")
        void returnsEmptyList() {
            when(priceHistoryRepository.findByDeviceIdOrderByCreatedDateDesc(999L))
                    .thenReturn(List.of());

            List<PriceHistoryResponse> result = pricingService.getPriceHistory(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("single history entry has no previous prices")
        void singleEntryNoPrevious() {
            PriceHistory h = makeHistory(1L, 100L, new BigDecimal("50.00"), new BigDecimal("40.00"),
                    LocalDateTime.of(2026, 1, 1, 0, 0));
            when(priceHistoryRepository.findByDeviceIdOrderByCreatedDateDesc(100L))
                    .thenReturn(List.of(h));

            List<PriceHistoryResponse> result = pricingService.getPriceHistory(100L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).listPrice()).isEqualByComparingTo("50.00");
            assertThat(result.get(0).previousListPrice()).isNull();
        }
    }
}
