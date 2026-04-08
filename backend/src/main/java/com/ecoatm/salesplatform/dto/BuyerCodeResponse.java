package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerCodeResponse {
    private Long id;
    private String code;
    private String buyerName;
    private String buyerCodeType; // Premium_Wholesale, Wholesale, Data_Wipe, etc.
}
