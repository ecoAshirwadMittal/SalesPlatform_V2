package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Audit row for admin qualify/unqualify actions on
 * {@link QualifiedBuyerCode}. Captures the old/new included flag and
 * qualification_type so cascade tests (round-2 eligibility) can correlate a
 * downstream change to the admin action that caused it.
 */
@Entity
@Table(name = "qualified_buyer_code_audit", schema = "buyer_mgmt")
@Getter
@Setter
public class QualifiedBuyerCodeAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "qualified_buyer_code_id", nullable = false)
    private Long qualifiedBuyerCodeId;

    @Column(name = "scheduling_auction_id")
    private Long schedulingAuctionId;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "old_included")
    private Boolean oldIncluded;

    @Column(name = "new_included")
    private Boolean newIncluded;

    @Column(name = "old_qualification_type", length = 20)
    private String oldQualificationType;

    @Column(name = "new_qualification_type", length = 20)
    private String newQualificationType;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_by_id")
    private Long changedById;
}
