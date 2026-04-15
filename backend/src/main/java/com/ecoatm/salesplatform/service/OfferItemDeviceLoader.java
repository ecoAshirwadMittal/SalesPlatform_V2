package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Batches device lookups for a collection of offer items. Shared by
 * OfferService and OfferReviewService to avoid duplicate implementations.
 */
@Component
public class OfferItemDeviceLoader {

    private final DeviceRepository deviceRepository;

    public OfferItemDeviceLoader(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        Set<Long> deviceIds = items.stream()
                .map(OfferItem::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) return Map.of();

        return deviceRepository.findAllById(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }
}
