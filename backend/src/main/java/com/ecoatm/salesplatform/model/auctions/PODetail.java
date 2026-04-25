package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "po_detail", schema = "auctions",
       uniqueConstraints = @UniqueConstraint(name = "uq_po_detail",
           columnNames = {"purchase_order_id", "product_id", "grade", "buyer_code_id"}))
public class PODetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_code_id")
    private BuyerCode buyerCode;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "grade", nullable = false, length = 200)
    private String grade;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "qty_cap")
    private Integer qtyCap;

    @Column(name = "price_fulfilled", precision = 14, scale = 4)
    private BigDecimal priceFulfilled;

    @Column(name = "qty_fulfilled")
    private Integer qtyFulfilled;

    @Column(name = "temp_buyer_code", length = 200)
    private String tempBuyerCode;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @PreUpdate void onUpdate() { this.changedDate = Instant.now(); }

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder v) { this.purchaseOrder = v; }
    public BuyerCode getBuyerCode() { return buyerCode; }
    public void setBuyerCode(BuyerCode v) { this.buyerCode = v; }
    public String getProductId() { return productId; }
    public void setProductId(String v) { this.productId = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
    public String getModelName() { return modelName; }
    public void setModelName(String v) { this.modelName = v; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { this.price = v; }
    public Integer getQtyCap() { return qtyCap; }
    public void setQtyCap(Integer v) { this.qtyCap = v; }
    public BigDecimal getPriceFulfilled() { return priceFulfilled; }
    public void setPriceFulfilled(BigDecimal v) { this.priceFulfilled = v; }
    public Integer getQtyFulfilled() { return qtyFulfilled; }
    public void setQtyFulfilled(Integer v) { this.qtyFulfilled = v; }
    public String getTempBuyerCode() { return tempBuyerCode; }
    public void setTempBuyerCode(String v) { this.tempBuyerCode = v; }
    public Instant getCreatedDate() { return createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public User getOwner() { return owner; }
    public void setOwner(User v) { this.owner = v; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User v) { this.changedBy = v; }
}
