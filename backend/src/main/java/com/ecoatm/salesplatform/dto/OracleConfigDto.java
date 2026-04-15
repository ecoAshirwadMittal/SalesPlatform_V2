package com.ecoatm.salesplatform.dto;

/**
 * Admin view of the Oracle ERP integration config.
 * Password is never serialized; {@code hasPassword} reflects whether
 * the server-side env var is populated.
 */
public record OracleConfigDto(
        Long id,
        String username,
        String password,
        Boolean hasPassword,
        String authPath,
        String createOrderPath,
        String createRmaPath,
        Integer timeoutMs,
        Boolean isCreateOrderApiOn,
        String updatedDate
) {}
