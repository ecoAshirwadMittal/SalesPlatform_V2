package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Compliance audit row written whenever a buyer code's {@code buyerCodeType}
 * actually changes on an admin edit. This is the modern port of the legacy
 * {@code EcoATM_BuyerManagement.BuyerCodeChangeLog} entity written by
 * {@code BCO_LogBuyerCodeChange} / {@code SUB_LogBuyerCodeTypeChange_Compliance}
 * — it maps the pre-existing {@code buyer_mgmt.buyer_code_change_logs} table
 * (created in V8, seeded in V18), NOT a new table.
 *
 * <p>The {@code id} column gained a sequence + column DEFAULT in V100 (the
 * legacy seed used explicit ids), so {@link GenerationType#IDENTITY} — the same
 * strategy {@link BuyerCode} uses after V66 — lets the DB fill it on insert.
 */
@Entity
@Table(name = "buyer_code_change_logs", schema = "buyer_mgmt")
@Getter
@Setter
public class BuyerCodeChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "old_buyer_code_type", length = 200)
    private String oldBuyerCodeType;

    @Column(name = "new_buyer_code_type", length = 200)
    private String newBuyerCodeType;

    /** Email of the editor at time of change (legacy {@code edited_by}). */
    @Column(name = "edited_by", length = 200)
    private String editedBy;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "changed_date", nullable = false)
    private LocalDateTime changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    /** JWT-derived id of the admin who applied the type change. */
    @Column(name = "changed_by_id")
    private Long changedById;
}
