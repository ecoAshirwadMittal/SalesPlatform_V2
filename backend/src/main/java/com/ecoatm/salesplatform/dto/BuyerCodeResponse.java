package com.ecoatm.salesplatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Buyer code summary returned by GET /api/v1/auth/buyer-codes.
 *
 * {@code codeType} is a derived field — computed from {@code buyerCodeType} so
 * callers never need to know which raw enum values map to which shell.
 * Mapping (authoritative):
 *   Premium_Wholesale                       → PWS
 *   Wholesale | Data_Wipe | any other value → AUCTION
 *
 * This centralises the mapping on the backend; the frontend just branches on
 * {@code "PWS"} vs {@code "AUCTION"}.
 */
@Data
@NoArgsConstructor
public class BuyerCodeResponse {
    private Long id;
    private String code;
    private String buyerName;
    private String buyerCodeType; // raw Mendix enum: Premium_Wholesale, Wholesale, Data_Wipe, etc.
    private String codeType;      // derived: "PWS" | "AUCTION"

    public BuyerCodeResponse(Long id, String code, String buyerName, String buyerCodeType) {
        this.id = id;
        this.code = code;
        this.buyerName = buyerName;
        this.buyerCodeType = buyerCodeType;
        this.codeType = "Premium_Wholesale".equals(buyerCodeType) ? "PWS" : "AUCTION";
    }
}
