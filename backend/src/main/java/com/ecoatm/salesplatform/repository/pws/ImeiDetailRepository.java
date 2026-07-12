package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.model.pws.ImeiDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ImeiDetailRepository extends JpaRepository<ImeiDetail, Long> {

    List<ImeiDetail> findByOfferItemOfferId(Long offerId);

    /**
     * Matches uploaded IMEI/serial values against shipped {@link com.ecoatm.salesplatform.model.pws.OfferItem}s
     * for a single buyer code — the modern port of the legacy {@code VAL_RMARequestFile}
     * lookup (Mendix filtered {@code OfferItem_BuyerCode = $BuyerCode} joined through
     * {@code IMEIDetail_OfferItem/IMEIDetail/IMEINumber}). The buyer-code scope now lives on
     * {@code offer.buyer_code_id}, reached via {@code imei_detail.offer_item_id → offer_item.offer_id}.
     * The OfferItem is fetch-joined so the caller can read device/price/SKU without an N+1.
     * A value may match on either {@code imei_number} or {@code serial_number} because the RMA
     * upload column is "IMEI/Serial".
     */
    @Query("""
            SELECT d FROM ImeiDetail d
            JOIN FETCH d.offerItem oi
            JOIN oi.offer o
            WHERE o.buyerCodeId = :buyerCodeId
              AND (d.imeiNumber IN :values OR d.serialNumber IN :values)
            """)
    List<ImeiDetail> findMatchesForBuyer(@Param("values") Collection<String> values,
                                         @Param("buyerCodeId") Long buyerCodeId);
}
