package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.CsvUploadResult;
import com.ecoatm.salesplatform.dto.PricingDeviceResponse;
import com.ecoatm.salesplatform.dto.PricingUpdateRequest;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricingService {

    private final DeviceRepository deviceRepository;

    public PricingService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
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
            String grade) {

        Specification<Device> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("isActive")));

            if (sku != null && !sku.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("sku")),
                        "%" + sku.toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                var join = root.join("category", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), category));
            }
            if (brand != null && !brand.isBlank()) {
                var join = root.join("brand", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), brand));
            }
            if (model != null && !model.isBlank()) {
                var join = root.join("model", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), model));
            }
            if (carrier != null && !carrier.isBlank()) {
                var join = root.join("carrier", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), carrier));
            }
            if (capacity != null && !capacity.isBlank()) {
                var join = root.join("capacity", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), capacity));
            }
            if (color != null && !color.isBlank()) {
                var join = root.join("color", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), color));
            }
            if (grade != null && !grade.isBlank()) {
                var join = root.join("grade", JoinType.INNER);
                preds.add(cb.equal(join.get("displayName"), grade));
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
                .collect(Collectors.toList());
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
        } catch (Exception e) {
            errors.add("Failed to read CSV: " + e.getMessage());
        }

        return new CsvUploadResult(totalRows, updatedCount, errors.size(), errors);
    }
}
