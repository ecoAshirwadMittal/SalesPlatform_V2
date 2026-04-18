package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "aggregated_inventory_totals", schema = "auctions")
public class AggregatedInventoryTotals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_id")
    private Long weekId;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "dw_total_quantity")
    private int dwTotalQuantity;

    @Column(name = "total_payout")
    private BigDecimal totalPayout;

    @Column(name = "dw_total_payout")
    private BigDecimal dwTotalPayout;

    @Column(name = "device_count")
    private int deviceCount;

    // Getters only (read-only entity)

    public Long getId() {
        return id;
    }

    public Long getWeekId() {
        return weekId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getDwTotalQuantity() {
        return dwTotalQuantity;
    }

    public BigDecimal getTotalPayout() {
        return totalPayout;
    }

    public BigDecimal getDwTotalPayout() {
        return dwTotalPayout;
    }

    public int getDeviceCount() {
        return deviceCount;
    }
}
