package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.BaseLookup;

public record MasterDataItemDto(
        Long id,
        String name,
        String displayName,
        Boolean isEnabled,
        Integer sortRank
) {
    public static MasterDataItemDto from(BaseLookup lookup) {
        return new MasterDataItemDto(
                lookup.getId(),
                lookup.getName(),
                lookup.getDisplayName(),
                lookup.getIsEnabled(),
                lookup.getSortRank()
        );
    }
}
