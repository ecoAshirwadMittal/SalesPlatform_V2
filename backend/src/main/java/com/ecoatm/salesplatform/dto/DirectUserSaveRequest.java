package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectUserSaveRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String userStatus;
    private boolean inactive;
    private String landingPagePreference;
    private List<Long> roleIds;
    private List<Long> buyerIds;
}
