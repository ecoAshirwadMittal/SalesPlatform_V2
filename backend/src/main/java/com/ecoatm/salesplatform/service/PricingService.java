package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.CsvUploadResult;
import com.ecoatm.salesplatform.dto.PriceHistoryResponse;
import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.dto.PricingUpdateRequest;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.mdm.PriceHistory;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.mdm.PriceHistoryRepository;
import com.ecoatm.salesplatform.security.CurrentPrincipal;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PricingService {

    private final DeviceRepository deviceRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final EntityManager entityManager;

    public PricingService(DeviceRepository deviceRepository,
                          PriceHistoryRepository priceHistoryRepository,
                          EntityManager entityManager) {
        this.deviceRepository = deviceRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public Page<PricingDeviceResponse> listPricingDevices(
            Pageable pageable,
            String sku,
            String category,
            String brand,
            String model,
            String carrier,
            String capacity,
            String color,
            String grade,
            BigDecimal currentListPrice,
            BigDecimal futureListPrice,
            BigDecimal currentMinPrice,
            BigDecimal futureMinPrice) {

        Specification<Device> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("isActive")));

            if (sku != null && !sku.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("sku")),
                        "%" + sku.toLowerCase() + "%"));
            }
            // Phase 2 (pws-data-center-port): swap Mendix's default `=`
            // string filters for case-insensitive `contains` on display names.
            if (category != null && !category.isBlank()) {
                var join = root.join("category", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + category.toLowerCase() + "%"));
            }
            if (brand != null && !brand.isBlank()) {
                var join = root.join("brand", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + brand.toLowerCase() + "%"));
            }
            if (model != null && !model.isBlank()) {
                var join = root.join("model", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + model.toLowerCase() + "%"));
            }
            if (carrier != null && !carrier.isBlank()) {
                var join = root.join("carrier", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + carrier.toLowerCase() + "%"));
            }
            if (capacity != null && !capacity.isBlank()) {
                var join = root.join("capacity", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + capacity.toLowerCase() + "%"));
            }
            if (color != null && !color.isBlank()) {
                var join = root.join("color", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + color.toLowerCase() + "%"));
            }
            if (grade != null && !grade.isBlank()) {
                var join = root.join("grade", JoinType.INNER);
                preds.add(cb.like(cb.lower(join.get("displayName")), "%" + grade.toLowerCase() + "%"));
            }

            if (currentListPrice != null) {
                preds.add(cb.equal(root.get("listPrice"), currentListPrice));
            }
            if (futureListPrice != null) {
                preds.add(cb.equal(root.get("futureListPrice"), futureListPrice));
            }
            if (currentMinPrice != null) {
                preds.add(cb.equal(root.get("minPrice"), currentMinPrice));
            }
            if (futureMinPrice != null) {
                preds.add(cb.equal(root.get("futureMinPrice"), futureMinPrice));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        return deviceRepository.findAll(spec, pageable)
                .map(PricingDeviceResponse::fromEntity);
    }

    @Transactional
    public PricingDeviceResponse updateFuturePrices(Long deviceId, BigDecimal futureListPrice, BigDecimal futureMinPrice) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
        device.setFutureListPrice(futureListPrice);
        device.setFutureMinPrice(futureMinPrice);
        Device saved = deviceRepository.save(device);
        return PricingDeviceResponse.fromEntity(saved);
    }

    @Transactional
    public List<PricingDeviceResponse> bulkUpdateFuturePrices(List<PricingUpdateRequest> requests) {
        return requests.stream()
                .map(req -> updateFuturePrices(req.getDeviceId(), req.getFutureListPrice(), req.getFutureMinPrice()))
                .toList();
    }

    @Transactional
    public CsvUploadResult processPricingCsv(InputStream csvStream) {
        List<String> errors = new ArrayList<>();
        int totalRows = 0;
        int updatedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // skip header
            if (header == null) {
                return new CsvUploadResult(0, 0, 0, List.of());
            }

            String line;
            int rowNum = 1; // header is row 1
            while ((line = reader.readLine()) != null) {
                rowNum++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                totalRows++;
                String[] cols = trimmed.split(",", -1);
                if (cols.length != 3) {
                    errors.add("Row " + rowNum + ": expected 3 columns, got " + cols.length);
                    continue;
                }

                String sku = cols[0].trim();
                if (!sku.isEmpty() && "=+-@".indexOf(sku.charAt(0)) >= 0) {
                    errors.add("Row " + rowNum + ": SKU contains disallowed leading character");
                    continue;
                }
                BigDecimal futureListPrice;
                BigDecimal futureMinPrice;
                try {
                    futureListPrice = new BigDecimal(cols[1].trim());
                    futureMinPrice = new BigDecimal(cols[2].trim());
                } catch (NumberFormatException e) {
                    errors.add("Row " + rowNum + ": invalid decimal value");
                    continue;
                }

                if (futureListPrice.compareTo(futureMinPrice) < 0) {
                    errors.add("Row " + rowNum + ": futureListPrice is less than futureMinPrice");
                    continue;
                }

                Optional<Device> deviceOpt = deviceRepository.findBySku(sku);
                if (deviceOpt.isEmpty()) {
                    errors.add("Row " + rowNum + ": SKU '" + sku + "' not found");
                    continue;
                }

                Device device = deviceOpt.get();
                device.setFutureListPrice(futureListPrice);
                device.setFutureMinPrice(futureMinPrice);
                deviceRepository.save(device);
                updatedCount++;
            }
        } catch (IOException e) {
            errors.add("Failed to read CSV: " + e.getMessage());
        }

        return new CsvUploadResult(totalRows, updatedCount, errors.size(), errors);
    }

    /**
     * Soft-delete a device: flips isActive off and writes a pws.admin_audit_log
     * row stamped with the current principal and an admin-supplied reason.
     * Replaces Mendix's untracked trash-delete button
     * (see docs/tasks/pws-data-center-port.md → "Legacy anti-patterns").
     */
    @Transactional
    public void softDeleteDevice(Long deviceId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required for device deletion");
        }
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
        if (Boolean.FALSE.equals(device.getIsActive())) {
            throw new IllegalStateException("Device already inactive: " + deviceId);
        }
        String before = "sku=" + device.getSku() + ",isActive=true";
        device.setIsActive(false);
        deviceRepository.save(device);

        String actor = CurrentPrincipal.displayName();
        entityManager.createNativeQuery(
                "INSERT INTO pws.admin_audit_log (entity_type, entity_id, action, reason, actor, before_state, after_state) "
                        + "VALUES ('Device', :id, 'SOFT_DELETE', :reason, :actor, :before, :after)")
                .setParameter("id", deviceId)
                .setParameter("reason", reason)
                .setParameter("actor", actor)
                .setParameter("before", before)
                .setParameter("after", "sku=" + device.getSku() + ",isActive=false")
                .executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<PriceHistoryResponse> getPriceHistory(Long deviceId) {
        List<PriceHistory> rows = priceHistoryRepository.findByDeviceIdOrderByCreatedDateDesc(deviceId);
        return PriceHistoryResponse.fromEntities(rows);
    }
}
