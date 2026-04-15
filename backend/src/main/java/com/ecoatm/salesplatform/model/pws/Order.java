package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
// "order" is a SQL reserved keyword — always quote in native queries: pws."order"
@Table(name = "\"order\"", schema = "pws")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @Column(name = "order_line")
    private String orderLine;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "oracle_status")
    private String oracleStatus;

    @Column(name = "ship_method")
    private String shipMethod;

    @Column(name = "shipped_total_qty")
    private Integer shippedTotalQty;

    @Column(name = "shipped_total_price")
    private BigDecimal shippedTotalPrice;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "oracle_http_code")
    private Integer oracleHttpCode;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "oracle_json_response", columnDefinition = "TEXT")
    private String oracleJsonResponse;

    @Column(name = "json_content", columnDefinition = "TEXT")
    private String jsonContent;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentDetail> shipments = new ArrayList<>();

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

    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getOrderLine() { return orderLine; }
    public void setOrderLine(String orderLine) { this.orderLine = orderLine; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getOracleStatus() { return oracleStatus; }
    public void setOracleStatus(String oracleStatus) { this.oracleStatus = oracleStatus; }

    public String getShipMethod() { return shipMethod; }
    public void setShipMethod(String shipMethod) { this.shipMethod = shipMethod; }

    public Integer getShippedTotalQty() { return shippedTotalQty; }
    public void setShippedTotalQty(Integer shippedTotalQty) { this.shippedTotalQty = shippedTotalQty; }

    public BigDecimal getShippedTotalPrice() { return shippedTotalPrice; }
    public void setShippedTotalPrice(BigDecimal shippedTotalPrice) { this.shippedTotalPrice = shippedTotalPrice; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getShipDate() { return shipDate; }
    public void setShipDate(LocalDateTime shipDate) { this.shipDate = shipDate; }

    public Integer getOracleHttpCode() { return oracleHttpCode; }
    public void setOracleHttpCode(Integer oracleHttpCode) { this.oracleHttpCode = oracleHttpCode; }

    public Boolean getIsSuccessful() { return isSuccessful; }
    public void setIsSuccessful(Boolean isSuccessful) { this.isSuccessful = isSuccessful; }

    public String getOracleJsonResponse() { return oracleJsonResponse; }
    public void setOracleJsonResponse(String oracleJsonResponse) { this.oracleJsonResponse = oracleJsonResponse; }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }

    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public List<ShipmentDetail> getShipments() { return shipments; }
    public void setShipments(List<ShipmentDetail> shipments) { this.shipments = shipments; }
}
