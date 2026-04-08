package com.ecoatm.salesplatform.model.pws;

import com.ecoatm.salesplatform.model.mdm.Device;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_lot", schema = "pws")
public class CaseLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "case_lot_id", nullable = false, unique = true)
    private String caseLotId;

    @Column(name = "case_lot_size", nullable = false)
    private Integer caseLotSize;

    @Column(name = "case_lot_price")
    private BigDecimal caseLotPrice;

    @Column(name = "case_lot_avl_qty")
    private Integer caseLotAvlQty;

    @Column(name = "case_lot_reserved_qty")
    private Integer caseLotReservedQty;

    @Column(name = "case_lot_atp_qty")
    private Integer caseLotAtpQty;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = updatedDate = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public String getCaseLotId() { return caseLotId; }
    public void setCaseLotId(String caseLotId) { this.caseLotId = caseLotId; }

    public Integer getCaseLotSize() { return caseLotSize; }
    public void setCaseLotSize(Integer caseLotSize) { this.caseLotSize = caseLotSize; }

    public BigDecimal getCaseLotPrice() { return caseLotPrice; }
    public void setCaseLotPrice(BigDecimal caseLotPrice) { this.caseLotPrice = caseLotPrice; }

    public Integer getCaseLotAvlQty() { return caseLotAvlQty; }
    public void setCaseLotAvlQty(Integer caseLotAvlQty) { this.caseLotAvlQty = caseLotAvlQty; }

    public Integer getCaseLotReservedQty() { return caseLotReservedQty; }
    public void setCaseLotReservedQty(Integer caseLotReservedQty) { this.caseLotReservedQty = caseLotReservedQty; }

    public Integer getCaseLotAtpQty() { return caseLotAtpQty; }
    public void setCaseLotAtpQty(Integer caseLotAtpQty) { this.caseLotAtpQty = caseLotAtpQty; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
}
