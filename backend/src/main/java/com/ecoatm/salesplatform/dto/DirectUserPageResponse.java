package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectUserPageResponse {
    private List<DirectUserListResponse> content;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
