package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "offer_and_orders_view", schema = "pws")
public class OrderHistoryView {

    @Id
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "offer_date")
    private LocalDateTime offerDate;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "ship_method")
    private String shipMethod;

    @Column(name = "sku_count")
    private Integer skuCount;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "buyer")
    private String buyer;

    @Column(name = "company")
    private String company;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "offer_order_type")
    private String offerOrderType;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "offer_id")
    private Long offerId;

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public LocalDateTime getOfferDate() { return offerDate; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public LocalDateTime getShipDate() { return shipDate; }
    public String getShipMethod() { return shipMethod; }
    public Integer getSkuCount() { return skuCount; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getBuyer() { return buyer; }
    public String getCompany() { return company; }
    public LocalDateTime getLastUpdateDate() { return lastUpdateDate; }
    public String getOfferOrderType() { return offerOrderType; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public Long getOfferId() { return offerId; }
}
