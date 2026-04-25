package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Single row in the Round 3 Bid Report by Buyer admin view.
 *
 * <p>Trims the entity down to the columns the admin grid actually shows:
 * buyer code + company + total quantity / payout + submission timestamp.
 * The bulky {@code report_json} blob is intentionally omitted — drill-down
 * would warrant a separate endpoint if/when that view is built.
 */
public record Round3ReportRow(
        Long id,
        Long auctionId,
        String buyerCode,
        String companyName,
        Integer totalQuantity,
        BigDecimal totalPayout,
        Instant submittedDatetime
) {
    public static Round3ReportRow from(Round3BuyerDataReport r) {
        return new Round3ReportRow(
                r.getId(),
                r.getAuctionId(),
                r.getBuyerCode(),
                r.getCompanyName(),
                r.getTotalQuantity(),
                r.getTotalPayout(),
                r.getSubmittedDatetime()
        );
    }
}
