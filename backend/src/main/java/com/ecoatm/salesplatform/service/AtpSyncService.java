package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto.FacilityInventory;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto.InventoryPage;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto.ItemInventory;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Replaces the legacy Mendix ACT_FullInventorySync + SUB_LoadPWSInventory_Deposco flow.
 *
 * Flow:
 *   1. Authenticate with Deposco to obtain an access token
 *   2. Fetch paginated facility inventory from Deposco Inventory API
 *   3. For each item: sum ATP across facilities, match to Device by SKU
 *   4. Update Device.availableQty and Device.atpQty where deltas exist
 *   5. Recalculate reserved quantities based on active ordered offer items
 *   6. Log sync statistics
 */
@Service
public class AtpSyncService {

    private static final Logger log = LoggerFactory.getLogger(AtpSyncService.class);

    private final DeviceRepository deviceRepository;
    private final RestTemplate restTemplate;

    @Value("${deposco.base-url:https://api.deposco.com}")
    private String deposcoBaseUrl;

    @Value("${deposco.username:api.user}")
    private String deposcoUsername;

    @Value("${deposco.password:}")
    private String deposcoPassword;

    @Value("${deposco.page-size:1000}")
    private int pageSize;

    public AtpSyncService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Execute a full ATP inventory sync.
     * Mirrors the legacy ACT_FullInventorySync microflow (ignoring feature flags).
     */
    @Transactional
    public AtpSyncResult fullInventorySync() {
        LocalDateTime syncBeginTime = LocalDateTime.now();
        log.info("PWS Full Inventory Sync started at {}", syncBeginTime);

        // Step 1: Fetch all inventory data from Deposco (paginated)
        Map<String, Integer> deposcoAtpBySku = fetchAllDeposcoInventory();

        // Step 2: Load all active devices into a lookup map
        List<Device> allDevices = deviceRepository.findByIsActiveTrue();
        Map<String, Device> deviceBySku = new HashMap<>();
        for (Device d : allDevices) {
            if (d.getSku() != null) {
                deviceBySku.put(d.getSku(), d);
            }
        }

        // Step 3: Walk Deposco items and apply ATP deltas
        int devicesUpdated = 0;
        List<String> missingSkus = new ArrayList<>();
        List<Device> updatedDevices = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : deposcoAtpBySku.entrySet()) {
            String itemNumber = entry.getKey();
            int newAtp = entry.getValue();

            Device device = deviceBySku.get(itemNumber);
            if (device == null) {
                missingSkus.add(itemNumber);
                continue;
            }

            int currentQty = device.getAvailableQty() != null ? device.getAvailableQty() : 0;
            if (currentQty != newAtp) {
                log.debug("Updating ATP for SKU:{} Current:{} New:{}", itemNumber, currentQty, newAtp);
                device.setAvailableQty(newAtp);
                device.setAtpQty(newAtp);
                device.setLastSyncTime(syncBeginTime);
                updatedDevices.add(device);
                devicesUpdated++;
            }
        }

        // Step 4: Batch-save updated devices
        if (!updatedDevices.isEmpty()) {
            deviceRepository.saveAll(updatedDevices);
            log.info("Saved {} device ATP updates", updatedDevices.size());
        }

        // Step 5: Recalculate reserved quantities
        // (Legacy: SUB_UpdateReservedQuanityPerDevice)
        updateReservedQuantities(updatedDevices);

        if (!missingSkus.isEmpty()) {
            log.warn("Deposco items not found in MDM: {}", missingSkus.size());
        }

        LocalDateTime syncEndTime = LocalDateTime.now();
        log.info("PWS Full Inventory Sync completed at {}. Updated {} devices, {} missing SKUs.",
                syncEndTime, devicesUpdated, missingSkus.size());

        // Build result
        AtpSyncResult result = new AtpSyncResult();
        result.setSyncStartTime(syncBeginTime);
        result.setSyncEndTime(syncEndTime);
        result.setTotalItemsReceived(deposcoAtpBySku.size());
        result.setDevicesUpdated(devicesUpdated);
        result.setDevicesMissing(missingSkus.size());
        result.setMissingSkus(missingSkus.size() > 50 ? missingSkus.subList(0, 50) : missingSkus);
        return result;
    }

    /**
     * Simulate the full Deposco inventory fetch for development/testing.
     * In production, replace this with the real Deposco HTTP calls.
     */
    public AtpSyncResult simulateSync(List<DeposcoInventoryDto.ItemInventory> items) {
        LocalDateTime syncBeginTime = LocalDateTime.now();
        log.info("Simulated ATP sync started with {} items", items.size());

        Map<String, Integer> atpBySku = new HashMap<>();
        for (ItemInventory item : items) {
            int totalAtp = 0;
            if (item.getFacilities() != null) {
                for (FacilityInventory f : item.getFacilities()) {
                    if (f.getAvailableToPromise() != null) {
                        totalAtp += f.getAvailableToPromise().setScale(0, RoundingMode.FLOOR).intValue();
                    }
                }
            }
            atpBySku.put(item.getItemNumber(), totalAtp);
        }

        return applyAtpUpdates(atpBySku, syncBeginTime);
    }

    // ── Private helpers ─────────────────────────────────────────────

    /**
     * Fetch all Deposco inventory pages and aggregate ATP per SKU.
     * Mirrors the paginated loop in ACT_FullInventorySync.
     */
    private Map<String, Integer> fetchAllDeposcoInventory() {
        Map<String, Integer> atpBySku = new HashMap<>();

        try {
            // Step 1: Get access token
            String accessToken = obtainDeposcoToken();
            if (accessToken == null) {
                log.error("Failed to obtain Deposco access token. Skipping external sync.");
                return atpBySku;
            }

            // Step 2: Calculate page count
            int deviceCount = (int) deviceRepository.count();
            int pageCount = Math.max(1, (int) Math.ceil((double) deviceCount / pageSize)) + 1;
            log.info("Fetching Deposco inventory: estimated {} pages for {} devices", pageCount, deviceCount);

            // Step 3: Paginated fetch
            int totalPagesProcessed = 0;
            for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
                try {
                    log.info("PWS Inventory Sync Deposco API. PageNum: {}", pageNo);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Void> request = new HttpEntity<>(headers);

                    String url = deposcoBaseUrl + "/inventory?page=" + pageNo + "&pageSize=" + pageSize;
                    ResponseEntity<InventoryPage> response = restTemplate.exchange(
                            url, HttpMethod.GET, request, InventoryPage.class);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        InventoryPage page = response.getBody();
                        if (page.getItems() != null) {
                            for (ItemInventory item : page.getItems()) {
                                int totalAtp = 0;
                                if (item.getFacilities() != null) {
                                    for (FacilityInventory f : item.getFacilities()) {
                                        if (f.getAvailableToPromise() != null) {
                                            totalAtp += f.getAvailableToPromise()
                                                    .setScale(0, RoundingMode.FLOOR).intValue();
                                        }
                                    }
                                }
                                atpBySku.put(item.getItemNumber(), totalAtp);
                            }
                        }
                        totalPagesProcessed++;

                        // Break early if we've exhausted pages
                        if (page.getItems() == null || page.getItems().isEmpty()) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.error("Deposco API error on page {}: {}", pageNo, e.getMessage());
                }
            }

            log.info("Deposco fetch complete: {} pages, {} unique SKUs", totalPagesProcessed, atpBySku.size());
        } catch (Exception e) {
            log.error("Failed to fetch Deposco inventory: {}", e.getMessage(), e);
        }

        return atpBySku;
    }

    /**
     * Authenticate with Deposco and return the bearer token.
     */
    private String obtainDeposcoToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("username", deposcoUsername, "password", deposcoPassword);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    deposcoBaseUrl + "/auth/token", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.error("Deposco authentication failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Apply ATP updates and reserved qty recalculation (shared between real and simulated sync).
     */
    @Transactional
    public AtpSyncResult applyAtpUpdates(Map<String, Integer> atpBySku, LocalDateTime syncBeginTime) {
        List<Device> allDevices = deviceRepository.findByIsActiveTrue();
        Map<String, Device> deviceBySku = new HashMap<>();
        for (Device d : allDevices) {
            if (d.getSku() != null) deviceBySku.put(d.getSku(), d);
        }

        int devicesUpdated = 0;
        List<String> missingSkus = new ArrayList<>();
        List<Device> updatedDevices = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atpBySku.entrySet()) {
            Device device = deviceBySku.get(entry.getKey());
            if (device == null) {
                missingSkus.add(entry.getKey());
                continue;
            }

            int currentQty = device.getAvailableQty() != null ? device.getAvailableQty() : 0;
            int newAtp = entry.getValue();
            if (currentQty != newAtp) {
                device.setAvailableQty(newAtp);
                device.setAtpQty(newAtp);
                device.setLastSyncTime(syncBeginTime);
                updatedDevices.add(device);
                devicesUpdated++;
            }
        }

        if (!updatedDevices.isEmpty()) {
            deviceRepository.saveAll(updatedDevices);
        }

        updateReservedQuantities(updatedDevices);

        AtpSyncResult result = new AtpSyncResult();
        result.setSyncStartTime(syncBeginTime);
        result.setSyncEndTime(LocalDateTime.now());
        result.setTotalItemsReceived(atpBySku.size());
        result.setDevicesUpdated(devicesUpdated);
        result.setDevicesMissing(missingSkus.size());
        result.setMissingSkus(missingSkus.size() > 50 ? missingSkus.subList(0, 50) : missingSkus);
        return result;
    }

    /**
     * Recalculate reserved and ATP quantities per device.
     * Mirrors legacy SUB_UpdateReservedQuanityPerDevice:
     *   - reservedQty = min(sum of ordered-offer-item quantities, availableQty)
     *   - atpQty = availableQty - reservedQty
     *
     * TODO: Wire to OfferItem repository once offer flow is active.
     * For now, sets reservedQty = 0 and atpQty = availableQty.
     */
    private void updateReservedQuantities(List<Device> devices) {
        for (Device device : devices) {
            int available = device.getAvailableQty() != null ? device.getAvailableQty() : 0;

            // TODO: Replace with actual offer-item reservation query:
            // int totalReserved = offerItemRepository.sumReservedQtyByDevice(device.getId());
            int totalReserved = 0;

            int reservedQty = Math.min(totalReserved, available);
            int atpQty = available - reservedQty;

            device.setReservedQty(reservedQty);
            device.setAtpQty(atpQty);
        }

        if (!devices.isEmpty()) {
            deviceRepository.saveAll(devices);
        }
    }
}
