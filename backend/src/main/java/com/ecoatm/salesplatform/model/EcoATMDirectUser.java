package com.ecoatm.salesplatform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ecoatm_direct_users", schema = "user_mgmt")
@Getter
@Setter
public class EcoATMDirectUser {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_buyer_role")
    private boolean buyerRole;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "overall_user_status")
    private String overallUserStatus;

    @Column(name = "landing_page_preference")
    private String landingPagePreference;

    @Column(name = "invited_date")
    private LocalDateTime invitedDate;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "changed_date")
    private LocalDateTime changedDate;

    @Column(name = "password_tmp_expires_at")
    private LocalDateTime passwordTmpExpiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Account account;
}
