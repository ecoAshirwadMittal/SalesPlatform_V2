package com.ecoatm.salesplatform.dto;

/** Response for POST /admin/inventory/weeks/{weekId}/sync. */
public record SyncTriggerResponse(String status, String source) {}
