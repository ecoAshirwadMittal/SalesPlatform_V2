package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "imei_detail", schema = "pws")
public class ImeiDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @Column(name = "imei_number")
    private String imeiNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "box_lpn_number")
    private String boxLpnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_item_id")
    private OfferItem offerItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_detail_id")
    private ShipmentDetail shipmentDetail;

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

    public String getImeiNumber() { return imeiNumber; }
    public void setImeiNumber(String imeiNumber) { this.imeiNumber = imeiNumber; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getBoxLpnNumber() { return boxLpnNumber; }
    public void setBoxLpnNumber(String boxLpnNumber) { this.boxLpnNumber = boxLpnNumber; }

    public OfferItem getOfferItem() { return offerItem; }
    public void setOfferItem(OfferItem offerItem) { this.offerItem = offerItem; }

    public ShipmentDetail getShipmentDetail() { return shipmentDetail; }
    public void setShipmentDetail(ShipmentDetail shipmentDetail) { this.shipmentDetail = shipmentDetail; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
