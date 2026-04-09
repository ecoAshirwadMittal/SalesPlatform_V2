package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.pws.FuturePriceConfig;
import com.ecoatm.salesplatform.repository.pws.FuturePriceConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuturePriceConfigServiceTest {

    @Mock
    private FuturePriceConfigRepository configRepository;

    @InjectMocks
    private FuturePriceConfigService futurePriceConfigService;

    private FuturePriceConfig makeConfig(Long id, LocalDateTime date) {
        FuturePriceConfig c = new FuturePriceConfig();
        c.setId(id);
        c.setFuturePriceDate(date);
        c.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        c.setUpdatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        return c;
    }

    @Test
    @DisplayName("getConfig returns the singleton config")
    void getConfigReturnsSingleton() {
        FuturePriceConfig config = makeConfig(1L, LocalDateTime.of(2026, 5, 1, 0, 0));
        when(configRepository.findAll()).thenReturn(List.of(config));

        FuturePriceConfig result = futurePriceConfigService.getConfig();

        assertThat(result.getFuturePriceDate()).isEqualTo(LocalDateTime.of(2026, 5, 1, 0, 0));
    }

    @Test
    @DisplayName("getConfig returns null date when no date set")
    void getConfigReturnsNullDate() {
        FuturePriceConfig config = makeConfig(1L, null);
        when(configRepository.findAll()).thenReturn(List.of(config));

        FuturePriceConfig result = futurePriceConfigService.getConfig();

        assertThat(result.getFuturePriceDate()).isNull();
    }

    @Test
    @DisplayName("updateFuturePriceDate updates and saves the singleton")
    void updateFuturePriceDate() {
        FuturePriceConfig config = makeConfig(1L, null);
        when(configRepository.findAll()).thenReturn(List.of(config));
        when(configRepository.save(any(FuturePriceConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime newDate = LocalDateTime.of(2026, 6, 15, 0, 0);
        FuturePriceConfig result = futurePriceConfigService.updateFuturePriceDate(newDate);

        assertThat(result.getFuturePriceDate()).isEqualTo(newDate);
        verify(configRepository).save(any(FuturePriceConfig.class));
    }

    @Test
    @DisplayName("updateFuturePriceDate allows clearing the date to null")
    void updateFuturePriceDateClear() {
        FuturePriceConfig config = makeConfig(1L, LocalDateTime.of(2026, 5, 1, 0, 0));
        when(configRepository.findAll()).thenReturn(List.of(config));
        when(configRepository.save(any(FuturePriceConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        FuturePriceConfig result = futurePriceConfigService.updateFuturePriceDate(null);

        assertThat(result.getFuturePriceDate()).isNull();
    }
}
