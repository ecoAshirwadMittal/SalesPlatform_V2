package com.ecoatm.salesplatform.dto;

/**
 * Single-row error from a bid import operation.
 *
 * @param row     1-based row number in the uploaded sheet (header = row 1,
 *                data rows start at row 2).
 * @param message Human-readable description of why the row was rejected.
 */
public record BidImportRowError(int row, String message) {}
