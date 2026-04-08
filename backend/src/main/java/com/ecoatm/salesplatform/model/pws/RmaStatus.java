package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rma_status", schema = "pws")
public class RmaStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_status", nullable = false, unique = true)
    private String systemStatus;

    @Column(name = "internal_status_text")
    private String internalStatusText;

    @Column(name = "external_status_text")
    private String externalStatusText;

    @Column(name = "internal_status_hex_code")
    private String internalStatusHexCode;

    @Column(name = "external_status_hex_code")
    private String externalStatusHexCode;

    @Column(name = "sales_status_header_hex_code")
    private String salesStatusHeaderHexCode;

    @Column(name = "sales_table_hover_hex_code")
    private String salesTableHoverHexCode;

    @Column(name = "status_grouped_to")
    private String statusGroupedTo;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_default")
    private Boolean isDefault;

    private String description;

    @Column(name = "status_verbiage_bidder")
    private String statusVerbiageBidder;

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

    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }

    public String getInternalStatusText() { return internalStatusText; }
    public void setInternalStatusText(String internalStatusText) { this.internalStatusText = internalStatusText; }

    public String getExternalStatusText() { return externalStatusText; }
    public void setExternalStatusText(String externalStatusText) { this.externalStatusText = externalStatusText; }

    public String getInternalStatusHexCode() { return internalStatusHexCode; }
    public void setInternalStatusHexCode(String internalStatusHexCode) { this.internalStatusHexCode = internalStatusHexCode; }

    public String getExternalStatusHexCode() { return externalStatusHexCode; }
    public void setExternalStatusHexCode(String externalStatusHexCode) { this.externalStatusHexCode = externalStatusHexCode; }

    public String getSalesStatusHeaderHexCode() { return salesStatusHeaderHexCode; }
    public void setSalesStatusHeaderHexCode(String salesStatusHeaderHexCode) { this.salesStatusHeaderHexCode = salesStatusHeaderHexCode; }

    public String getSalesTableHoverHexCode() { return salesTableHoverHexCode; }
    public void setSalesTableHoverHexCode(String salesTableHoverHexCode) { this.salesTableHoverHexCode = salesTableHoverHexCode; }

    public String getStatusGroupedTo() { return statusGroupedTo; }
    public void setStatusGroupedTo(String statusGroupedTo) { this.statusGroupedTo = statusGroupedTo; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatusVerbiageBidder() { return statusVerbiageBidder; }
    public void setStatusVerbiageBidder(String statusVerbiageBidder) { this.statusVerbiageBidder = statusVerbiageBidder; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
