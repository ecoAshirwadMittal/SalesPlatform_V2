package com.ecoatm.salesplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectUserDetailResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String overallUserStatus;
    private String userStatus;
    private boolean inactive;
    private boolean localUser;
    private boolean buyerRole;
    private String landingPagePreference;
    private LocalDateTime invitedDate;
    private LocalDateTime lastInviteSent;
    private LocalDateTime activationDate;
    private LocalDateTime lastLogin;
    private List<Long> roleIds;
    private List<Long> buyerIds;
}
