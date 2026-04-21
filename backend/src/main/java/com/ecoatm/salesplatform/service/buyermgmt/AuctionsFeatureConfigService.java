package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuctionsFeatureConfigService {

    private static final BigDecimal DEFAULT_MINIMUM_ALLOWED_BID = new BigDecimal("2.00");
    private static final int DEFAULT_ROUND2_OFFSET_MINUTES = 360;
    private static final int DEFAULT_ROUND3_OFFSET_MINUTES = 180;

    private final AuctionsFeatureConfigRepository repo;

    public AuctionsFeatureConfigService(AuctionsFeatureConfigRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AuctionsFeatureConfig getOrCreate() {
        return repo.findSingleton().orElseGet(this::insertDefaults);
    }

    private AuctionsFeatureConfig insertDefaults() {
        AuctionsFeatureConfig config = new AuctionsFeatureConfig();
        config.setMinimumAllowedBid(DEFAULT_MINIMUM_ALLOWED_BID);
        config.setAuctionRound2MinutesOffset(DEFAULT_ROUND2_OFFSET_MINUTES);
        config.setAuctionRound3MinutesOffset(DEFAULT_ROUND3_OFFSET_MINUTES);
        config.setSendAuctionDataToSnowflake(false);
        return repo.save(config);
    }
}
