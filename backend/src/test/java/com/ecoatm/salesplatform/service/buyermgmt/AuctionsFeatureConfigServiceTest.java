package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionsFeatureConfigServiceTest {

    @Mock
    AuctionsFeatureConfigRepository repo;

    AuctionsFeatureConfigService service;

    @BeforeEach
    void setUp() {
        service = new AuctionsFeatureConfigService(repo);
    }

    @Test
    void getOrCreate_returnsExistingSingleton() {
        AuctionsFeatureConfig existing = new AuctionsFeatureConfig();
        existing.setId(1L);
        existing.setMinimumAllowedBid(new BigDecimal("2.50"));
        when(repo.findSingleton()).thenReturn(Optional.of(existing));

        AuctionsFeatureConfig result = service.getOrCreate();

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    void getOrCreate_insertsDefaultsWhenMissing() {
        when(repo.findSingleton()).thenReturn(Optional.empty());
        when(repo.save(any(AuctionsFeatureConfig.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AuctionsFeatureConfig result = service.getOrCreate();

        ArgumentCaptor<AuctionsFeatureConfig> captor =
                ArgumentCaptor.forClass(AuctionsFeatureConfig.class);
        verify(repo).save(captor.capture());
        AuctionsFeatureConfig saved = captor.getValue();

        assertThat(saved.getMinimumAllowedBid()).isEqualByComparingTo("2.00");
        assertThat(saved.getAuctionRound2MinutesOffset()).isEqualTo(360);
        assertThat(saved.getAuctionRound3MinutesOffset()).isEqualTo(180);
        assertThat(saved.isSendAuctionDataToSnowflake()).isFalse();
        assertThat(result).isSameAs(saved);
    }
}
