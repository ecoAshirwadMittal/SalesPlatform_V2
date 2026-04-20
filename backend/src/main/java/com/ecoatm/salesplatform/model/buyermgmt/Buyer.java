package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "buyers", schema = "buyer_mgmt")
@Getter
@Setter
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "company_name")
    private String companyName;

    /**
     * Stored as VARCHAR(8) in the DB. Legacy rows may contain null or
     * empty strings — JPA will surface those as a null enum value, and
     * any service that needs Mendix parity must normalize null to
     * {@link BuyerStatus#Disabled} at the read boundary.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BuyerStatus status;

    @Column(name = "is_special_buyer")
    private boolean specialBuyer;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "changed_date")
    private LocalDateTime changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;

    @Column(name = "entity_owner")
    private String entityOwner;
}
