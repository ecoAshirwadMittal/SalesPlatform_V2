package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Header row for a partial-credit request. The 3 has_* booleans encode
 * which reasons the buyer flagged; the three line tables (Missing /
 * Wrong / Encumbered) hang off this row.
 *
 * <p>Order metadata (party_name, dates) is captured from the Snowflake
 * manifest at submit time so the request stays self-contained.
 */
@Entity
@Table(name = "credit_requests", schema = "partial_credit")
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true, length = 40)
    private String requestNumber;

    @Column(name = "request_date", nullable = false) private Instant requestDate = Instant.now();
    @Column(name = "submitted_date") private Instant submittedDate;

    @Column(name = "status_id", nullable = false) private Long statusId;

    @Column(name = "order_number", nullable = false, length = 200) private String orderNumber;
    @Column(name = "party_name", length = 200) private String partyName;
    @Column(name = "order_created_date") private Instant orderCreatedDate;
    @Column(name = "order_shipped_date") private Instant orderShippedDate;

    @Column(name = "has_missing_device", nullable = false)    private Boolean hasMissingDevice = Boolean.FALSE;
    @Column(name = "has_wrong_device", nullable = false)      private Boolean hasWrongDevice = Boolean.FALSE;
    @Column(name = "has_encumbered_device", nullable = false) private Boolean hasEncumberedDevice = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_damaged", nullable = false, length = 20)
    private ShipmentDamaged shipmentDamaged = ShipmentDamaged.NOT_ANSWERED;

    @Column(name = "total_devices", nullable = false)   private Integer totalDevices = 0;
    @Column(name = "requested_skus", nullable = false)  private Integer requestedSkus = 0;
    @Column(name = "requested_qty", nullable = false)   private Integer requestedQty = 0;
    @Column(name = "requested_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal requestedTotal = BigDecimal.ZERO;
    @Column(name = "approved_skus", nullable = false)   private Integer approvedSkus = 0;
    @Column(name = "approved_qty", nullable = false)    private Integer approvedQty = 0;
    @Column(name = "approved_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal approvedTotal = BigDecimal.ZERO;

    @Column(name = "reviewed_by_id")     private Long reviewedById;
    @Column(name = "review_completed_on") private Instant reviewCompletedOn;
    @Column(name = "submitted_by_id")    private Long submittedById;

    @Column(name = "buyer_code_id", nullable = false) private Long buyerCodeId;
    @Column(name = "buyer_id") private Long buyerId;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "created_by_id") private Long createdById;
    @Column(name = "changed_by_id") private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRequestNumber() { return requestNumber; }
    public void setRequestNumber(String v) { this.requestNumber = v; }
    public Instant getRequestDate() { return requestDate; }
    public void setRequestDate(Instant v) { this.requestDate = v; }
    public Instant getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Instant v) { this.submittedDate = v; }
    public Long getStatusId() { return statusId; }
    public void setStatusId(Long v) { this.statusId = v; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String v) { this.orderNumber = v; }
    public String getPartyName() { return partyName; }
    public void setPartyName(String v) { this.partyName = v; }
    public Instant getOrderCreatedDate() { return orderCreatedDate; }
    public void setOrderCreatedDate(Instant v) { this.orderCreatedDate = v; }
    public Instant getOrderShippedDate() { return orderShippedDate; }
    public void setOrderShippedDate(Instant v) { this.orderShippedDate = v; }
    public Boolean getHasMissingDevice() { return hasMissingDevice; }
    public void setHasMissingDevice(Boolean v) { this.hasMissingDevice = v; }
    public Boolean getHasWrongDevice() { return hasWrongDevice; }
    public void setHasWrongDevice(Boolean v) { this.hasWrongDevice = v; }
    public Boolean getHasEncumberedDevice() { return hasEncumberedDevice; }
    public void setHasEncumberedDevice(Boolean v) { this.hasEncumberedDevice = v; }
    public ShipmentDamaged getShipmentDamaged() { return shipmentDamaged; }
    public void setShipmentDamaged(ShipmentDamaged v) { this.shipmentDamaged = v; }
    public Integer getTotalDevices() { return totalDevices; }
    public void setTotalDevices(Integer v) { this.totalDevices = v; }
    public Integer getRequestedSkus() { return requestedSkus; }
    public void setRequestedSkus(Integer v) { this.requestedSkus = v; }
    public Integer getRequestedQty() { return requestedQty; }
    public void setRequestedQty(Integer v) { this.requestedQty = v; }
    public BigDecimal getRequestedTotal() { return requestedTotal; }
    public void setRequestedTotal(BigDecimal v) { this.requestedTotal = v; }
    public Integer getApprovedSkus() { return approvedSkus; }
    public void setApprovedSkus(Integer v) { this.approvedSkus = v; }
    public Integer getApprovedQty() { return approvedQty; }
    public void setApprovedQty(Integer v) { this.approvedQty = v; }
    public BigDecimal getApprovedTotal() { return approvedTotal; }
    public void setApprovedTotal(BigDecimal v) { this.approvedTotal = v; }
    public Long getReviewedById() { return reviewedById; }
    public void setReviewedById(Long v) { this.reviewedById = v; }
    public Instant getReviewCompletedOn() { return reviewCompletedOn; }
    public void setReviewCompletedOn(Instant v) { this.reviewCompletedOn = v; }
    public Long getSubmittedById() { return submittedById; }
    public void setSubmittedById(Long v) { this.submittedById = v; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long v) { this.buyerCodeId = v; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long v) { this.buyerId = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
