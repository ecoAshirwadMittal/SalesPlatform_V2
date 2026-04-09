package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "future_price_config", schema = "pws")
public class FuturePriceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "future_price_date")
    private LocalDateTime futurePriceDate;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFuturePriceDate() { return futurePriceDate; }
    public void setFuturePriceDate(LocalDateTime futurePriceDate) { this.futurePriceDate = futurePriceDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
