package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import jakarta.persistence.*;

/**
 * Config row backing the 5 system statuses (DRAFT / PENDING_APPROVAL /
 * UNDER_REVIEW / APPROVED / DECLINED). Admins can tune the display
 * text + color from the status-configuration page without changing the
 * {@link SystemStatus} enum.
 */
@Entity
@Table(name = "credit_request_statuses", schema = "partial_credit")
public class CreditRequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_status", nullable = false, unique = true, length = 40)
    private SystemStatus systemStatus;

    @Column(name = "internal_status_text", nullable = false, length = 100) private String internalStatusText;
    @Column(name = "external_status_text", nullable = false, length = 100) private String externalStatusText;
    @Column(name = "color_hex", nullable = false, length = 7)               private String colorHex = "#888888";
    @Column(name = "sort_order", nullable = false)                          private Integer sortOrder = 0;
    @Column(name = "show_in_user_counters", nullable = false)               private Boolean showInUserCounters = Boolean.TRUE;
    @Column(name = "is_default", nullable = false)                          private Boolean isDefault = Boolean.FALSE;
    @Column(name = "status_grouped_to", length = 40)                        private String statusGroupedTo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SystemStatus getSystemStatus() { return systemStatus; }
    public void setSystemStatus(SystemStatus s) { this.systemStatus = s; }
    public String getInternalStatusText() { return internalStatusText; }
    public void setInternalStatusText(String v) { this.internalStatusText = v; }
    public String getExternalStatusText() { return externalStatusText; }
    public void setExternalStatusText(String v) { this.externalStatusText = v; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String v) { this.colorHex = v; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer v) { this.sortOrder = v; }
    public Boolean getShowInUserCounters() { return showInUserCounters; }
    public void setShowInUserCounters(Boolean v) { this.showInUserCounters = v; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean v) { this.isDefault = v; }
    public String getStatusGroupedTo() { return statusGroupedTo; }
    public void setStatusGroupedTo(String v) { this.statusGroupedTo = v; }
}
