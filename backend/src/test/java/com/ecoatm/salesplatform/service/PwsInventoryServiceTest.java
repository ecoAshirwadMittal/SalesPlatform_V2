package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.DeviceRequest;
import com.ecoatm.salesplatform.dto.DeviceResponse;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PwsInventoryServiceTest {

    @Mock private DeviceRepository deviceRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ModelRepository modelRepository;
    @Mock private ConditionRepository conditionRepository;
    @Mock private CapacityRepository capacityRepository;
    @Mock private CarrierRepository carrierRepository;
    @Mock private ColorRepository colorRepository;
    @Mock private GradeRepository gradeRepository;

    @InjectMocks
    private PwsInventoryService pwsInventoryService;

    // --- Helpers ---

    private Device makeDevice(Long id, String sku) {
        Device d = new Device();
        d.setId(id);
        d.setSku(sku);
        d.setIsActive(true);
        return d;
    }

    private DeviceRequest makeRequest(String sku) {
        DeviceRequest req = new DeviceRequest();
        req.setSku(sku);
        req.setDescription("Test device");
        req.setListPrice(BigDecimal.TEN);
        return req;
    }

    private Brand makeBrand(Long id, String name) {
        Brand b = new Brand();
        b.setId(id);
        b.setName(name);
        b.setDisplayName(name);
        return b;
    }

    // ── createDevice ───────────────────────────────────────────────

    @Nested
    @DisplayName("createDevice")
    class CreateDevice {

        @Test
        @DisplayName("creates device successfully")
        void createDevice_success() {
            DeviceRequest req = makeRequest("NEW-SKU");
            when(deviceRepository.existsBySku("NEW-SKU")).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(1L);
                return d;
            });

            DeviceResponse response = pwsInventoryService.createDevice(req);

            assertThat(response).isNotNull();
            assertThat(response.getSku()).isEqualTo("NEW-SKU");
            verify(deviceRepository).save(any(Device.class));
        }

        @Test
        @DisplayName("duplicate SKU throws IllegalArgumentException")
        void createDevice_duplicateSku_throws() {
            DeviceRequest req = makeRequest("DUP-SKU");
            when(deviceRepository.existsBySku("DUP-SKU")).thenReturn(true);

            assertThatThrownBy(() -> pwsInventoryService.createDevice(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    // ── updateDevice ───────────────────────────────────────────────

    @Nested
    @DisplayName("updateDevice")
    class UpdateDevice {

        @Test
        @DisplayName("updates device successfully")
        void updateDevice_success() {
            Device existing = makeDevice(1L, "EXIST-SKU");
            DeviceRequest req = makeRequest("UPD-SKU");
            when(deviceRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

            DeviceResponse response = pwsInventoryService.updateDevice(1L, req);

            assertThat(response).isNotNull();
            assertThat(response.getSku()).isEqualTo("UPD-SKU");
        }

        @Test
        @DisplayName("not found throws IllegalArgumentException")
        void updateDevice_notFound_throws() {
            when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pwsInventoryService.updateDevice(999L, makeRequest("X")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device not found");
        }
    }

    // ── getDeviceBySku / getDeviceById / listActiveDevices ──────────

    @Nested
    @DisplayName("device lookups")
    class DeviceLookups {

        @Test
        @DisplayName("getDeviceBySku returns device")
        void getDeviceBySku_success() {
            Device d = makeDevice(1L, "SKU-001");
            when(deviceRepository.findBySku("SKU-001")).thenReturn(Optional.of(d));

            DeviceResponse resp = pwsInventoryService.getDeviceBySku("SKU-001");
            assertThat(resp.getSku()).isEqualTo("SKU-001");
        }

        @Test
        @DisplayName("getDeviceBySku not found throws")
        void getDeviceBySku_notFound_throws() {
            when(deviceRepository.findBySku("MISSING")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pwsInventoryService.getDeviceBySku("MISSING"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device not found for SKU");
        }

        @Test
        @DisplayName("getDeviceById returns device")
        void getDeviceById_success() {
            Device d = makeDevice(1L, "SKU-001");
            when(deviceRepository.findById(1L)).thenReturn(Optional.of(d));

            DeviceResponse resp = pwsInventoryService.getDeviceById(1L);
            assertThat(resp.getSku()).isEqualTo("SKU-001");
        }

        @Test
        @DisplayName("getDeviceById not found throws")
        void getDeviceById_notFound_throws() {
            when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pwsInventoryService.getDeviceById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device not found");
        }

        @Test
        @DisplayName("listActiveDevices returns mapped list")
        void listActiveDevices_returnsList() {
            when(deviceRepository.findByIsActiveTrue())
                    .thenReturn(List.of(makeDevice(1L, "A"), makeDevice(2L, "B")));

            List<DeviceResponse> result = pwsInventoryService.listActiveDevices();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSku()).isEqualTo("A");
        }
    }

    // ── listFilteredDevices ────────────────────────────────────────

    @Nested
    @DisplayName("listFilteredDevices")
    class ListFilteredDevices {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("returns devices when all filters provided")
        void listFilteredDevices_allFilters_returnsDevices() {
            Device d = makeDevice(1L, "PWS-001");
            d.setItemType("PWS");
            d.setAtpQty(10);
            d.setIsActive(true);
            when(deviceRepository.findAll(any(Specification.class))).thenReturn(List.of(d));

            List<DeviceResponse> result = pwsInventoryService.listFilteredDevices("PWS", "A_YYY", 0);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSku()).isEqualTo("PWS-001");
            verify(deviceRepository).findAll(any(Specification.class));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("passes specification to repository")
        void listFilteredDevices_passesSpecification() {
            when(deviceRepository.findAll(any(Specification.class))).thenReturn(List.of());

            pwsInventoryService.listFilteredDevices("PWS", "A_YYY", 0);

            ArgumentCaptor<Specification<Device>> captor = ArgumentCaptor.forClass(Specification.class);
            verify(deviceRepository).findAll(captor.capture());
            assertThat(captor.getValue()).isNotNull();
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("works with no filters (all null)")
        void listFilteredDevices_noFilters_returnsAll() {
            Device d = makeDevice(1L, "ANY");
            d.setIsActive(true);
            when(deviceRepository.findAll(any(Specification.class))).thenReturn(List.of(d));

            List<DeviceResponse> result = pwsInventoryService.listFilteredDevices(null, null, null);

            assertThat(result).hasSize(1);
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("returns empty list when repository returns empty")
        void listFilteredDevices_empty() {
            when(deviceRepository.findAll(any(Specification.class))).thenReturn(List.of());

            List<DeviceResponse> result = pwsInventoryService.listFilteredDevices("PWS", "A_YYY", 0);

            assertThat(result).isEmpty();
        }
    }

    // ── Lookup resolution ──────────────────────────────────────────

    @Nested
    @DisplayName("lookup resolution")
    class LookupResolution {

        @Test
        @DisplayName("resolves brand by ID")
        void resolveBrand_byId() {
            DeviceRequest req = makeRequest("SKU-LU");
            req.setBrandId(5L);
            Brand brand = makeBrand(5L, "Apple");
            when(brandRepository.findById(5L)).thenReturn(Optional.of(brand));
            when(deviceRepository.existsBySku("SKU-LU")).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(1L);
                return d;
            });

            pwsInventoryService.createDevice(req);

            verify(brandRepository).findById(5L);
            verify(brandRepository, never()).findByName(anyString());
        }

        @Test
        @DisplayName("resolves brand by name (existing)")
        void resolveBrand_byName_existing() {
            DeviceRequest req = makeRequest("SKU-LU2");
            req.setBrandName("Samsung");
            Brand brand = makeBrand(10L, "Samsung");
            when(brandRepository.findByName("Samsung")).thenReturn(Optional.of(brand));
            when(deviceRepository.existsBySku("SKU-LU2")).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(2L);
                return d;
            });

            pwsInventoryService.createDevice(req);

            verify(brandRepository).findByName("Samsung");
            verify(brandRepository, never()).save(any(Brand.class));
        }

        @Test
        @DisplayName("auto-creates brand by name when not found")
        void resolveBrand_byName_autoCreates() {
            DeviceRequest req = makeRequest("SKU-LU3");
            req.setBrandName("NewBrand");
            Brand created = makeBrand(99L, "NewBrand");
            when(brandRepository.findByName("NewBrand")).thenReturn(Optional.empty());
            when(brandRepository.save(any(Brand.class))).thenReturn(created);
            when(deviceRepository.existsBySku("SKU-LU3")).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(3L);
                return d;
            });

            pwsInventoryService.createDevice(req);

            verify(brandRepository).save(any(Brand.class));
        }

        @Test
        @DisplayName("null/blank name resolves to null")
        void resolveBrand_blankName_returnsNull() {
            DeviceRequest req = makeRequest("SKU-LU4");
            // no brandId or brandName set
            when(deviceRepository.existsBySku("SKU-LU4")).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(4L);
                return d;
            });

            pwsInventoryService.createDevice(req);

            verify(brandRepository, never()).findById(any());
            verify(brandRepository, never()).findByName(anyString());
        }
    }

    // ── bulkCreateDevices ──────────────────────────────────────────

    @Nested
    @DisplayName("bulkCreateDevices")
    class BulkCreateDevices {

        @Test
        @DisplayName("creates new devices for unknown SKUs")
        void bulkCreate_newDevices() {
            DeviceRequest req1 = makeRequest("BULK-1");
            DeviceRequest req2 = makeRequest("BULK-2");
            when(deviceRepository.findBySku("BULK-1")).thenReturn(Optional.empty());
            when(deviceRepository.findBySku("BULK-2")).thenReturn(Optional.empty());
            when(deviceRepository.existsBySku(anyString())).thenReturn(false);
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
                Device d = inv.getArgument(0);
                d.setId(System.nanoTime());
                return d;
            });

            List<DeviceResponse> results = pwsInventoryService.bulkCreateDevices(List.of(req1, req2));

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("updates existing devices for known SKUs")
        void bulkCreate_updatesExisting() {
            DeviceRequest req = makeRequest("EXIST-SKU");
            Device existing = makeDevice(1L, "EXIST-SKU");
            when(deviceRepository.findBySku("EXIST-SKU")).thenReturn(Optional.of(existing));
            when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

            List<DeviceResponse> results = pwsInventoryService.bulkCreateDevices(List.of(req));

            assertThat(results).hasSize(1);
            // Should update, not create new
            verify(deviceRepository, never()).existsBySku("EXIST-SKU");
        }
    }
}
