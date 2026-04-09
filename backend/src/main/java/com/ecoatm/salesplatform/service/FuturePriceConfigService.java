package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.pws.FuturePriceConfig;
import com.ecoatm.salesplatform.repository.pws.FuturePriceConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FuturePriceConfigService {

    private final FuturePriceConfigRepository configRepository;

    public FuturePriceConfigService(FuturePriceConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Transactional(readOnly = true)
    public FuturePriceConfig getConfig() {
        List<FuturePriceConfig> all = configRepository.findAll();
        if (all.isEmpty()) {
            FuturePriceConfig fresh = new FuturePriceConfig();
            fresh.setCreatedDate(LocalDateTime.now());
            fresh.setUpdatedDate(LocalDateTime.now());
            return configRepository.save(fresh);
        }
        return all.get(0);
    }

    @Transactional
    public FuturePriceConfig updateFuturePriceDate(LocalDateTime futurePriceDate) {
        FuturePriceConfig config = getConfig();
        config.setFuturePriceDate(futurePriceDate);
        config.setUpdatedDate(LocalDateTime.now());
        return configRepository.save(config);
    }
}
