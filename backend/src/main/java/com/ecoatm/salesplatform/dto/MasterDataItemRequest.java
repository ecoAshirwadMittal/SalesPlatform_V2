package com.ecoatm.salesplatform.dto;

public record MasterDataItemRequest(
        String name,
        String displayName,
        Boolean isEnabled,
        Integer sortRank
) {}
