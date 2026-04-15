package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rma_item", schema = "pws")
public class RmaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rma_id", nullable = false)
    private Rma rma;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "order_id")
    private Long orderId;

    private String imei;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "sale_price", precision = 14, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(length = 10)
    private String status;

    @Column(name = "status_display")
    private String statusDisplay;

    @Column(name = "decline_reason")
    private String declineReason;

    @Column(name = "entity_owner")
    private String entityOwner;

    @Column(name = "entity_changer")
    private String entityChanger;

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

    public Rma getRma() { return rma; }
    public void setRma(Rma rma) { this.rma = rma; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public LocalDateTime getShipDate() { return shipDate; }
    public void setShipDate(LocalDateTime shipDate) { this.shipDate = shipDate; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusDisplay() { return statusDisplay; }
    public void setStatusDisplay(String statusDisplay) { this.statusDisplay = statusDisplay; }

    public String getDeclineReason() { return declineReason; }
    public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }

    public String getEntityOwner() { return entityOwner; }
    public void setEntityOwner(String entityOwner) { this.entityOwner = entityOwner; }

    public String getEntityChanger() { return entityChanger; }
    public void setEntityChanger(String entityChanger) { this.entityChanger = entityChanger; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
