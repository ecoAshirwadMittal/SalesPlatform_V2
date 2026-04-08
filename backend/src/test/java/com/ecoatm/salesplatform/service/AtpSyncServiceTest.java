package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.AtpSyncResult;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto.FacilityInventory;
import com.ecoatm.salesplatform.dto.DeposcoInventoryDto.ItemInventory;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtpSyncServiceTest {

    @Mock private DeviceRepository deviceRepository;

    @InjectMocks
    private AtpSyncService atpSyncService;

    // --- Helpers ---

    private Device makeDevice(Long id, String sku, int availableQty) {
        Device d = new Device();
        d.setId(id);
        d.setSku(sku);
        d.setAvailableQty(availableQty);
        d.setAtpQty(availableQty);
        d.setIsActive(true);
        return d;
    }

    private ItemInventory makeItem(String itemNumber, BigDecimal... atpValues) {
        ItemInventory item = new ItemInventory();
        item.setItemNumber(itemNumber);
        List<FacilityInventory> facilities = new ArrayList<>();
        for (BigDecimal atp : atpValues) {
            FacilityInventory f = new FacilityInventory();
            f.setAvailableToPromise(atp);
            facilities.add(f);
        }
        item.setFacilities(facilities);
        return item;
    }

    // ── applyAtpUpdates ─────────────────────────────────────────────

    @Nested
    @DisplayName("applyAtpUpdates")
    class ApplyAtpUpdates {

        @Test
        @DisplayName("updates matching SKUs with new ATP values")
        void applyAtpUpdates_updatesMatchingSku() {
            Device dev1 = makeDevice(1L, "SKU-A", 10);
            Device dev2 = makeDevice(2L, "SKU-B", 20);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev1, dev2));
            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            Map<String, Integer> atpBySku = new HashMap<>();
            atpBySku.put("SKU-A", 50);  // changed from 10 to 50
            atpBySku.put("SKU-B", 20);  // unchanged

            AtpSyncResult result = atpSyncService.applyAtpUpdates(atpBySku, LocalDateTime.now());

            assertThat(result.getDevicesUpdated()).isEqualTo(1);
            assertThat(result.getDevicesMissing()).isZero();
            assertThat(dev1.getAvailableQty()).isEqualTo(50);
            assertThat(dev1.getAtpQty()).isEqualTo(50);
            // dev2 unchanged
            assertThat(dev2.getAvailableQty()).isEqualTo(20);
        }

        @Test
        @DisplayName("reports missing SKUs not in device catalog")
        void applyAtpUpdates_reportsMissingSkus() {
            Device dev1 = makeDevice(1L, "SKU-A", 10);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev1));

            Map<String, Integer> atpBySku = new HashMap<>();
            atpBySku.put("SKU-A", 10);      // unchanged
            atpBySku.put("UNKNOWN-1", 5);    // missing
            atpBySku.put("UNKNOWN-2", 8);    // missing

            AtpSyncResult result = atpSyncService.applyAtpUpdates(atpBySku, LocalDateTime.now());

            assertThat(result.getDevicesMissing()).isEqualTo(2);
            assertThat(result.getMissingSkus()).containsExactlyInAnyOrder("UNKNOWN-1", "UNKNOWN-2");
            assertThat(result.getDevicesUpdated()).isZero();
        }

        @Test
        @DisplayName("handles empty ATP map gracefully")
        void applyAtpUpdates_emptyMap() {
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of());

            AtpSyncResult result = atpSyncService.applyAtpUpdates(Map.of(), LocalDateTime.now());

            assertThat(result.getDevicesUpdated()).isZero();
            assertThat(result.getDevicesMissing()).isZero();
            assertThat(result.getTotalItemsReceived()).isZero();
        }

        @Test
        @DisplayName("handles null availableQty on device (treats as 0)")
        void applyAtpUpdates_nullAvailableQty() {
            Device dev = makeDevice(1L, "SKU-NULL", 0);
            dev.setAvailableQty(null);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));
            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            Map<String, Integer> atpBySku = new HashMap<>();
            atpBySku.put("SKU-NULL", 25);

            AtpSyncResult result = atpSyncService.applyAtpUpdates(atpBySku, LocalDateTime.now());

            assertThat(result.getDevicesUpdated()).isEqualTo(1);
            assertThat(dev.getAvailableQty()).isEqualTo(25);
        }

        @Test
        @DisplayName("truncates missingSkus to 50 entries")
        void applyAtpUpdates_truncatesMissingSkus() {
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of());

            Map<String, Integer> atpBySku = new HashMap<>();
            for (int i = 0; i < 60; i++) {
                atpBySku.put("MISSING-" + i, i);
            }

            AtpSyncResult result = atpSyncService.applyAtpUpdates(atpBySku, LocalDateTime.now());

            assertThat(result.getDevicesMissing()).isEqualTo(60);
            assertThat(result.getMissingSkus()).hasSize(50);
        }

        @Test
        @DisplayName("sets lastSyncTime on updated devices")
        void applyAtpUpdates_setsLastSyncTime() {
            Device dev = makeDevice(1L, "SKU-SYNC", 0);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));
            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            LocalDateTime syncTime = LocalDateTime.of(2026, 4, 7, 12, 0);
            Map<String, Integer> atpBySku = Map.of("SKU-SYNC", 100);

            atpSyncService.applyAtpUpdates(atpBySku, syncTime);

            assertThat(dev.getLastSyncTime()).isEqualTo(syncTime);
        }
    }

    // ── simulateSync ────────────────────────────────────────────────

    @Nested
    @DisplayName("simulateSync")
    class SimulateSync {

        @Test
        @DisplayName("aggregates ATP across multiple facilities")
        void simulateSync_aggregatesFacilities() {
            Device dev = makeDevice(1L, "SKU-MULTI", 0);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));
            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            // Item with 2 facilities: 10.7 + 20.3 → floor → 10 + 20 = 30
            ItemInventory item = makeItem("SKU-MULTI",
                    BigDecimal.valueOf(10.7), BigDecimal.valueOf(20.3));

            AtpSyncResult result = atpSyncService.simulateSync(List.of(item));

            assertThat(result.getDevicesUpdated()).isEqualTo(1);
            assertThat(dev.getAvailableQty()).isEqualTo(30);
        }

        @Test
        @DisplayName("handles null facilities gracefully")
        void simulateSync_nullFacilities() {
            Device dev = makeDevice(1L, "SKU-NF", 0);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));

            ItemInventory item = new ItemInventory();
            item.setItemNumber("SKU-NF");
            item.setFacilities(null);

            AtpSyncResult result = atpSyncService.simulateSync(List.of(item));

            // ATP = 0, same as current → no update
            assertThat(result.getDevicesUpdated()).isZero();
        }

        @Test
        @DisplayName("handles null availableToPromise in facility")
        void simulateSync_nullAtpInFacility() {
            Device dev = makeDevice(1L, "SKU-NATP", 0);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));

            FacilityInventory f1 = new FacilityInventory();
            f1.setAvailableToPromise(BigDecimal.valueOf(15));
            FacilityInventory f2 = new FacilityInventory();
            f2.setAvailableToPromise(null); // null ATP

            ItemInventory item = new ItemInventory();
            item.setItemNumber("SKU-NATP");
            item.setFacilities(List.of(f1, f2));

            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            AtpSyncResult result = atpSyncService.simulateSync(List.of(item));

            assertThat(result.getDevicesUpdated()).isEqualTo(1);
            assertThat(dev.getAvailableQty()).isEqualTo(15);
        }
    }

    // ── updateReservedQuantities (via applyAtpUpdates) ──────────────

    @Nested
    @DisplayName("updateReservedQuantities")
    class UpdateReservedQuantities {

        @Test
        @DisplayName("sets reservedQty=0 and atpQty=available (TODO stub)")
        void updateReservedQuantities_setsZeroReserved() {
            Device dev = makeDevice(1L, "SKU-RES", 10);
            when(deviceRepository.findByIsActiveTrue()).thenReturn(List.of(dev));
            when(deviceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            Map<String, Integer> atpBySku = Map.of("SKU-RES", 100);

            atpSyncService.applyAtpUpdates(atpBySku, LocalDateTime.now());

            // After updateReservedQuantities: reserved=0, atp=available
            assertThat(dev.getReservedQty()).isZero();
            assertThat(dev.getAtpQty()).isEqualTo(100);
        }
    }
}
