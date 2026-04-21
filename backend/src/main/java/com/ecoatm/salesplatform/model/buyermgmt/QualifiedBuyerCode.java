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
@Table(name = "qualified_buyer_codes", schema = "buyer_mgmt")
@Getter
@Setter
public class QualifiedBuyerCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "scheduling_auction_id", nullable = false)
    private Long schedulingAuctionId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "qualification_type", nullable = false, length = 13)
    private QualificationType qualificationType = QualificationType.Not_Qualified;

    @Column(name = "included", nullable = false)
    private boolean included;

    @Column(name = "submitted", nullable = false)
    private boolean submitted;

    @Column(name = "submitted_datetime")
    private LocalDateTime submittedDatetime;

    @Column(name = "opened_dashboard", nullable = false)
    private boolean openedDashboard;

    @Column(name = "opened_dashboard_datetime")
    private LocalDateTime openedDashboardDatetime;

    @Column(name = "is_special_treatment", nullable = false)
    private boolean specialTreatment;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "changed_date", nullable = false)
    private LocalDateTime changedDate = LocalDateTime.now();

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;
}
