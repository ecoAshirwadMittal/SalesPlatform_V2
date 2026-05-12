package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * One row in the sales-rep "Submit on behalf — pick a buyer user"
 * dropdown. Lists buyer-side ecoATM Direct users associated with at
 * least one of the buyer companies on the chosen buyer code.
 */
public record BuyerUserOption(Long userId, String displayName, String email) {
}
