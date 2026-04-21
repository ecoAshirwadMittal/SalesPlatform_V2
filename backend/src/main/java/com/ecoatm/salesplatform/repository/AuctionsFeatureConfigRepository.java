package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Accessor for the {@code buyer_mgmt.auctions_feature_config} singleton row.
 *
 * <p>The V63 seed guarantees exactly one row exists in every environment, so
 * {@link #findSingleton()} is safe to call without bootstrapping.
 */
@Repository
public interface AuctionsFeatureConfigRepository extends JpaRepository<AuctionsFeatureConfig, Long> {

    default Optional<AuctionsFeatureConfig> findSingleton() {
        return findAll().stream().findFirst();
    }
}
