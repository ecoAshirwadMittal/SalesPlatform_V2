package com.ecoatm.salesplatform.model.mdm;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "device", schema = "mdm")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @Column(unique = true)
    private String sku;

    @Column(name = "device_code")
    private String deviceCode;

    private String description;

    // Pricing
    @Column(name = "list_price")
    private BigDecimal listPrice;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "future_list_price")
    private BigDecimal futureListPrice;

    @Column(name = "future_min_price")
    private BigDecimal futureMinPrice;

    // Inventory
    @Column(name = "available_qty")
    private Integer availableQty;

    @Column(name = "reserved_qty")
    private Integer reservedQty;

    @Column(name = "atp_qty")
    private Integer atpQty;

    private BigDecimal weight;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Flattened FKs to lookup tables
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id")
    private Condition condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capacity_id")
    private Capacity capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getFutureListPrice() { return futureListPrice; }
    public void setFutureListPrice(BigDecimal futureListPrice) { this.futureListPrice = futureListPrice; }

    public BigDecimal getFutureMinPrice() { return futureMinPrice; }
    public void setFutureMinPrice(BigDecimal futureMinPrice) { this.futureMinPrice = futureMinPrice; }

    public Integer getAvailableQty() { return availableQty; }
    public void setAvailableQty(Integer availableQty) { this.availableQty = availableQty; }

    public Integer getReservedQty() { return reservedQty; }
    public void setReservedQty(Integer reservedQty) { this.reservedQty = reservedQty; }

    public Integer getAtpQty() { return atpQty; }
    public void setAtpQty(Integer atpQty) { this.atpQty = atpQty; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public Capacity getCapacity() { return capacity; }
    public void setCapacity(Capacity capacity) { this.capacity = capacity; }

    public Carrier getCarrier() { return carrier; }
    public void setCarrier(Carrier carrier) { this.carrier = carrier; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
}
