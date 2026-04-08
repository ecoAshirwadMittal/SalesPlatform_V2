package com.ecoatm.salesplatform.dto;

import java.util.List;

public class RmaDetailResponse {

    private RmaResponse rma;
    private List<RmaItemResponse> items;

    public RmaDetailResponse(RmaResponse rma, List<RmaItemResponse> items) {
        this.rma = rma;
        this.items = items;
    }

    public RmaResponse getRma() { return rma; }
    public List<RmaItemResponse> getItems() { return items; }
}
