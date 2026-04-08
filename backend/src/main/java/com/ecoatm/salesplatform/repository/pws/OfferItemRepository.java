package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {

    Optional<OfferItem> findByOfferAndSku(Offer offer, String sku);

    List<OfferItem> findByOffer(Offer offer);

    void deleteByOfferAndSku(Offer offer, String sku);

    void deleteByOffer(Offer offer);
}
