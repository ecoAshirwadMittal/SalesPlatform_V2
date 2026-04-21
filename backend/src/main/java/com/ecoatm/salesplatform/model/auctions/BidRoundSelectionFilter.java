package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Round qualification criteria for Round 2 and Round 3. Exactly two rows
 * exist (one per round); admins edit them via
 * {@code GET/PUT /api/v1/admin/auctions/round-filters/{round}}.
 *
 * <p>Ported from Mendix {@code AuctionUI.BidRoundSelectionFilter}. The
 * schema lives in V59 — no new migration is introduced for Phase D.
 *
 * <p>Enum storage uses {@link EnumType#STRING} to stay aligned with the
 * {@code chk_brsf_regular_qual} / {@code chk_brsf_regular_inventory}
 * CHECK constraints and the 2026-04-19 ADR rationale (ordinals would
 * silently reinterpret existing rows if an enum constant is reordered).
 */
@Entity
@Table(name = "bid_round_selection_filters", schema = "auctions")
public class BidRoundSelectionFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "target_percent", precision = 10, scale = 4)
    private BigDecimal targetPercent;

    @Column(name = "target_value", precision = 14, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "total_value_floor", precision = 14, scale = 2)
    private BigDecimal totalValueFloor;

    @Column(name = "merged_grade1", length = 30)
    private String mergedGrade1;

    @Column(name = "merged_grade2", length = 30)
    private String mergedGrade2;

    @Column(name = "merged_grade3", length = 30)
    private String mergedGrade3;

    @Column(name = "stb_allow_all_buyers_override", nullable = false)
    private Boolean stbAllowAllBuyersOverride = Boolean.FALSE;

    @Column(name = "stb_include_all_inventory", nullable = false)
    private Boolean stbIncludeAllInventory = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "regular_buyer_qualification", length = 30, nullable = false)
    private RegularBuyerQualification regularBuyerQualification = RegularBuyerQualification.Only_Qualified;

    @Enumerated(EnumType.STRING)
    @Column(name = "regular_buyer_inventory_options", length = 40, nullable = false)
    private RegularBuyerInventoryOption regularBuyerInventoryOptions =
            RegularBuyerInventoryOption.InventoryRound1QualifiedBids;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate;

    public Long getId() { return id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }

    public BigDecimal getTargetPercent() { return targetPercent; }
    public void setTargetPercent(BigDecimal targetPercent) { this.targetPercent = targetPercent; }

    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

    public BigDecimal getTotalValueFloor() { return totalValueFloor; }
    public void setTotalValueFloor(BigDecimal totalValueFloor) { this.totalValueFloor = totalValueFloor; }

    public String getMergedGrade1() { return mergedGrade1; }
    public void setMergedGrade1(String mergedGrade1) { this.mergedGrade1 = mergedGrade1; }

    public String getMergedGrade2() { return mergedGrade2; }
    public void setMergedGrade2(String mergedGrade2) { this.mergedGrade2 = mergedGrade2; }

    public String getMergedGrade3() { return mergedGrade3; }
    public void setMergedGrade3(String mergedGrade3) { this.mergedGrade3 = mergedGrade3; }

    public Boolean getStbAllowAllBuyersOverride() { return stbAllowAllBuyersOverride; }
    public void setStbAllowAllBuyersOverride(Boolean stbAllowAllBuyersOverride) {
        this.stbAllowAllBuyersOverride = stbAllowAllBuyersOverride;
    }

    public Boolean getStbIncludeAllInventory() { return stbIncludeAllInventory; }
    public void setStbIncludeAllInventory(Boolean stbIncludeAllInventory) {
        this.stbIncludeAllInventory = stbIncludeAllInventory;
    }

    public RegularBuyerQualification getRegularBuyerQualification() { return regularBuyerQualification; }
    public void setRegularBuyerQualification(RegularBuyerQualification regularBuyerQualification) {
        this.regularBuyerQualification = regularBuyerQualification;
    }

    public RegularBuyerInventoryOption getRegularBuyerInventoryOptions() { return regularBuyerInventoryOptions; }
    public void setRegularBuyerInventoryOptions(RegularBuyerInventoryOption regularBuyerInventoryOptions) {
        this.regularBuyerInventoryOptions = regularBuyerInventoryOptions;
    }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }
}
