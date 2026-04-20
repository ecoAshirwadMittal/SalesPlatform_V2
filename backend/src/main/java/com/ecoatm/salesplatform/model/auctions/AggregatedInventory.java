package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "aggregated_inventory", schema = "auctions")
public class AggregatedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "ecoid2")
    private String ecoid2;

    @Column(name = "name")
    private String name;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "category")
    private String category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "carrier_id")
    private Long carrierId;

    @Column(name = "week_id")
    private Long weekId;

    @Column(name = "merged_grade")
    private String mergedGrade;

    @Column(name = "datawipe")
    private boolean datawipe;

    @Column(name = "is_total_quantity_modified")
    private boolean totalQuantityModified;

    @Column(name = "is_deprecated")
    private boolean deprecated;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "dw_total_quantity")
    private int dwTotalQuantity;

    @Column(name = "avg_payout")
    private BigDecimal avgPayout;

    @Column(name = "total_payout")
    private BigDecimal totalPayout;

    @Column(name = "dw_avg_payout")
    private BigDecimal dwAvgPayout;

    @Column(name = "dw_total_payout")
    private BigDecimal dwTotalPayout;

    @Column(name = "avg_target_price")
    private BigDecimal avgTargetPrice;

    @Column(name = "dw_avg_target_price")
    private BigDecimal dwAvgTargetPrice;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "changed_date")
    private Instant changedDate;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getEcoid2() {
        return ecoid2;
    }

    public void setEcoid2(String ecoid2) {
        this.ecoid2 = ecoid2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getWeekId() {
        return weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public String getMergedGrade() {
        return mergedGrade;
    }

    public void setMergedGrade(String mergedGrade) {
        this.mergedGrade = mergedGrade;
    }

    public boolean isDatawipe() {
        return datawipe;
    }

    public void setDatawipe(boolean datawipe) {
        this.datawipe = datawipe;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getDwTotalQuantity() {
        return dwTotalQuantity;
    }

    public void setDwTotalQuantity(int dwTotalQuantity) {
        this.dwTotalQuantity = dwTotalQuantity;
    }

    public BigDecimal getAvgTargetPrice() {
        return avgTargetPrice;
    }

    public void setAvgTargetPrice(BigDecimal avgTargetPrice) {
        this.avgTargetPrice = avgTargetPrice;
    }

    public BigDecimal getDwAvgTargetPrice() {
        return dwAvgTargetPrice;
    }

    public void setDwAvgTargetPrice(BigDecimal dwAvgTargetPrice) {
        this.dwAvgTargetPrice = dwAvgTargetPrice;
    }

    public Instant getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Instant changedDate) {
        this.changedDate = changedDate;
    }

    public boolean isTotalQuantityModified() {
        return totalQuantityModified;
    }

    public void setTotalQuantityModified(boolean totalQuantityModified) {
        this.totalQuantityModified = totalQuantityModified;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
