package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImeiDetailRepository extends JpaRepository<ImeiDetail, Long> {

    List<ImeiDetail> findByOfferItemOfferId(Long offerId);
}
