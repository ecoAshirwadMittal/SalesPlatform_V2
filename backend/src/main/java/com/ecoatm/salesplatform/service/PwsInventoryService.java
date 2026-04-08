package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.DeviceRequest;
import com.ecoatm.salesplatform.dto.DeviceResponse;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PwsInventoryService {

    private final DeviceRepository deviceRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;
    private final ConditionRepository conditionRepository;
    private final CapacityRepository capacityRepository;
    private final CarrierRepository carrierRepository;
    private final ColorRepository colorRepository;
    private final GradeRepository gradeRepository;

    public PwsInventoryService(
            DeviceRepository deviceRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            ModelRepository modelRepository,
            ConditionRepository conditionRepository,
            CapacityRepository capacityRepository,
            CarrierRepository carrierRepository,
            ColorRepository colorRepository,
            GradeRepository gradeRepository) {
        this.deviceRepository = deviceRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.modelRepository = modelRepository;
        this.conditionRepository = conditionRepository;
        this.capacityRepository = capacityRepository;
        this.carrierRepository = carrierRepository;
        this.colorRepository = colorRepository;
        this.gradeRepository = gradeRepository;
    }

    /**
     * Create a new device in the MDM catalog.
     * Lookup fields are resolved by ID first, then by name. If a name is provided
     * and doesn't exist, it is auto-created (upsert behaviour for catalog enrichment).
     */
    @Transactional
    public DeviceResponse createDevice(DeviceRequest req) {
        if (deviceRepository.existsBySku(req.getSku())) {
            throw new IllegalArgumentException("Device with SKU '" + req.getSku() + "' already exists.");
        }

        Device device = new Device();
        mapRequestToEntity(req, device);
        Device saved = deviceRepository.save(device);
        return DeviceResponse.fromEntity(saved);
    }

    /**
     * Update an existing device by ID.
     */
    @Transactional
    public DeviceResponse updateDevice(Long id, DeviceRequest req) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
        mapRequestToEntity(req, device);
        Device saved = deviceRepository.save(device);
        return DeviceResponse.fromEntity(saved);
    }

    /**
     * Bulk-create devices from an inventory sync payload.
     */
    @Transactional
    public List<DeviceResponse> bulkCreateDevices(List<DeviceRequest> requests) {
        return requests.stream()
                .map(this::createOrUpdateDevice)
                .collect(Collectors.toList());
    }

    /**
     * Find a device by SKU.
     */
    @Transactional(readOnly = true)
    public DeviceResponse getDeviceBySku(String sku) {
        Device device = deviceRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Device not found for SKU: " + sku));
        return DeviceResponse.fromEntity(device);
    }

    /**
     * Find a device by ID.
     */
    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
        return DeviceResponse.fromEntity(device);
    }

    /**
     * List all active devices.
     */
    @Transactional(readOnly = true)
    public List<DeviceResponse> listActiveDevices() {
        return deviceRepository.findByIsActiveTrue().stream()
                .map(DeviceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Internal helpers ──────────────────────────────────────────────

    private DeviceResponse createOrUpdateDevice(DeviceRequest req) {
        return deviceRepository.findBySku(req.getSku())
                .map(existing -> {
                    mapRequestToEntity(req, existing);
                    return DeviceResponse.fromEntity(deviceRepository.save(existing));
                })
                .orElseGet(() -> createDevice(req));
    }

    private void mapRequestToEntity(DeviceRequest req, Device device) {
        device.setSku(req.getSku());
        device.setDeviceCode(req.getDeviceCode());
        device.setDescription(req.getDescription());
        device.setListPrice(req.getListPrice());
        device.setMinPrice(req.getMinPrice());
        device.setFutureListPrice(req.getFutureListPrice());
        device.setFutureMinPrice(req.getFutureMinPrice());
        device.setAvailableQty(req.getAvailableQty());
        device.setReservedQty(req.getReservedQty());
        device.setAtpQty(req.getAtpQty());
        device.setWeight(req.getWeight());
        device.setItemType(req.getItemType());

        device.setBrand(resolveBrand(req.getBrandId(), req.getBrandName()));
        device.setCategory(resolveCategory(req.getCategoryId(), req.getCategoryName()));
        device.setModel(resolveModel(req.getModelId(), req.getModelName()));
        device.setCondition(resolveCondition(req.getConditionId(), req.getConditionName()));
        device.setCapacity(resolveCapacity(req.getCapacityId(), req.getCapacityName()));
        device.setCarrier(resolveCarrier(req.getCarrierId(), req.getCarrierName()));
        device.setColor(resolveColor(req.getColorId(), req.getColorName()));
        device.setGrade(resolveGrade(req.getGradeId(), req.getGradeName()));
    }

    // ── Lookup resolution: ID → name → auto-create ───────────────────

    private Brand resolveBrand(Long id, String name) {
        if (id != null) return brandRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return brandRepository.findByName(name).orElseGet(() -> {
            Brand b = new Brand();
            b.setName(name);
            b.setDisplayName(name);
            return brandRepository.save(b);
        });
    }

    private Category resolveCategory(Long id, String name) {
        if (id != null) return categoryRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return categoryRepository.findByName(name).orElseGet(() -> {
            Category c = new Category();
            c.setName(name);
            c.setDisplayName(name);
            return categoryRepository.save(c);
        });
    }

    private Model resolveModel(Long id, String name) {
        if (id != null) return modelRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return modelRepository.findByName(name).orElseGet(() -> {
            Model m = new Model();
            m.setName(name);
            m.setDisplayName(name);
            return modelRepository.save(m);
        });
    }

    private Condition resolveCondition(Long id, String name) {
        if (id != null) return conditionRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return conditionRepository.findByName(name).orElseGet(() -> {
            Condition c = new Condition();
            c.setName(name);
            c.setDisplayName(name);
            return conditionRepository.save(c);
        });
    }

    private Capacity resolveCapacity(Long id, String name) {
        if (id != null) return capacityRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return capacityRepository.findByName(name).orElseGet(() -> {
            Capacity c = new Capacity();
            c.setName(name);
            c.setDisplayName(name);
            return capacityRepository.save(c);
        });
    }

    private Carrier resolveCarrier(Long id, String name) {
        if (id != null) return carrierRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return carrierRepository.findByName(name).orElseGet(() -> {
            Carrier c = new Carrier();
            c.setName(name);
            c.setDisplayName(name);
            return carrierRepository.save(c);
        });
    }

    private Color resolveColor(Long id, String name) {
        if (id != null) return colorRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return colorRepository.findByName(name).orElseGet(() -> {
            Color c = new Color();
            c.setName(name);
            c.setDisplayName(name);
            return colorRepository.save(c);
        });
    }

    private Grade resolveGrade(Long id, String name) {
        if (id != null) return gradeRepository.findById(id).orElse(null);
        if (name == null || name.isBlank()) return null;
        return gradeRepository.findByName(name).orElseGet(() -> {
            Grade g = new Grade();
            g.setName(name);
            g.setDisplayName(name);
            return gradeRepository.save(g);
        });
    }
}
