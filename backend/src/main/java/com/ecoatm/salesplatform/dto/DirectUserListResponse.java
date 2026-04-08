package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectUserListResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String overallUserStatus;
    private Long submissionId;
    private String buyers;
    private String roles;
    private LocalDate changedDate;
}
