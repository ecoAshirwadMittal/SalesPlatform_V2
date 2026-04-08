package com.ecoatm.salesplatform.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a completed ATP sync run.
 */
public class AtpSyncResult {

    private LocalDateTime syncStartTime;
    private LocalDateTime syncEndTime;
    private int totalPagesProcessed;
    private int totalItemsReceived;
    private int devicesUpdated;
    private int devicesMissing;
    private List<String> missingSkus;

    public LocalDateTime getSyncStartTime() { return syncStartTime; }
    public void setSyncStartTime(LocalDateTime syncStartTime) { this.syncStartTime = syncStartTime; }

    public LocalDateTime getSyncEndTime() { return syncEndTime; }
    public void setSyncEndTime(LocalDateTime syncEndTime) { this.syncEndTime = syncEndTime; }

    public int getTotalPagesProcessed() { return totalPagesProcessed; }
    public void setTotalPagesProcessed(int totalPagesProcessed) { this.totalPagesProcessed = totalPagesProcessed; }

    public int getTotalItemsReceived() { return totalItemsReceived; }
    public void setTotalItemsReceived(int totalItemsReceived) { this.totalItemsReceived = totalItemsReceived; }

    public int getDevicesUpdated() { return devicesUpdated; }
    public void setDevicesUpdated(int devicesUpdated) { this.devicesUpdated = devicesUpdated; }

    public int getDevicesMissing() { return devicesMissing; }
    public void setDevicesMissing(int devicesMissing) { this.devicesMissing = devicesMissing; }

    public List<String> getMissingSkus() { return missingSkus; }
    public void setMissingSkus(List<String> missingSkus) { this.missingSkus = missingSkus; }
}
