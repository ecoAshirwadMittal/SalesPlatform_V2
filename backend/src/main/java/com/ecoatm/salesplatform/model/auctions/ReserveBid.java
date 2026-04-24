package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid", schema = "auctions",
       uniqueConstraints = @UniqueConstraint(name = "uq_reserve_bid_product_grade",
                                             columnNames = {"product_id", "grade"}))
public class ReserveBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;
    @Column(name = "product_id", nullable = false, length = 100) private String productId;
    @Column(nullable = false, length = 200) private String grade;
    @Column(length = 200) private String brand;
    @Column(length = 200) private String model;

    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal bid = BigDecimal.ZERO;
    @Column(name = "last_update_datetime", nullable = false) private Instant lastUpdateDatetime = Instant.now();

    @Column(name = "last_awarded_min_price", precision = 14, scale = 4)
    private BigDecimal lastAwardedMinPrice;

    @Column(name = "last_awarded_week", length = 20)   private String lastAwardedWeek;
    @Column(name = "bid_valid_week_date", length = 20) private String bidValidWeekDate;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getBid() { return bid; }
    public void setBid(BigDecimal bid) { this.bid = bid; }
    public Instant getLastUpdateDatetime() { return lastUpdateDatetime; }
    public void setLastUpdateDatetime(Instant v) { this.lastUpdateDatetime = v; }
    public BigDecimal getLastAwardedMinPrice() { return lastAwardedMinPrice; }
    public void setLastAwardedMinPrice(BigDecimal v) { this.lastAwardedMinPrice = v; }
    public String getLastAwardedWeek() { return lastAwardedWeek; }
    public void setLastAwardedWeek(String v) { this.lastAwardedWeek = v; }
    public String getBidValidWeekDate() { return bidValidWeekDate; }
    public void setBidValidWeekDate(String v) { this.bidValidWeekDate = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
