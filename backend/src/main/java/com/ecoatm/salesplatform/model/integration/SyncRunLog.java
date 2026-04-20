package com.ecoatm.salesplatform.model.integration;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_run_log", schema = "integration")
public class SyncRunLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sync_type", nullable = false, length = 32)
    private String syncType;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_items_received", nullable = false)
    private Integer totalItemsReceived = 0;

    @Column(name = "devices_updated", nullable = false)
    private Integer devicesUpdated = 0;

    @Column(name = "devices_missing", nullable = false)
    private Integer devicesMissing = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "triggered_by", length = 200)
    private String triggeredBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getTotalItemsReceived() { return totalItemsReceived; }
    public void setTotalItemsReceived(Integer totalItemsReceived) { this.totalItemsReceived = totalItemsReceived; }

    public Integer getDevicesUpdated() { return devicesUpdated; }
    public void setDevicesUpdated(Integer devicesUpdated) { this.devicesUpdated = devicesUpdated; }

    public Integer getDevicesMissing() { return devicesMissing; }
    public void setDevicesMissing(Integer devicesMissing) { this.devicesMissing = devicesMissing; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
