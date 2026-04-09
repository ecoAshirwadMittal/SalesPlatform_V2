package com.ecoatm.salesplatform.dto;

import java.util.List;

public record CsvUploadResult(
    int totalRows,
    int updatedCount,
    int errorCount,
    List<String> errors
) {}
