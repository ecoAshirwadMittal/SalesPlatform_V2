package com.ecoatm.salesplatform.service.partialcredit;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Resolves a buyer-entered "received-device" string (the
 * {@code actualImeiOrModel} field on {@code WrongDeviceLine}) to a
 * canonical {@code (ecoatmCode, mergedGrade, brand, model)} tuple via
 * {@code mdm.device}.
 *
 * <p>Phase-1 strategy (this chunk):
 * <ol>
 *   <li>Treat a 15-digit numeric string as an IMEI. {@code mdm.device}
 *       carries no IMEI column today (only {@code pws.rma_item.imei}
 *       does), so IMEI-shaped input currently returns
 *       {@link Optional#empty()} — Phase-2 will introduce a TAC lookup
 *       table to bridge IMEI → device. The empty result tells the
 *       recommendation engine to flag the line for reviewer attention.</li>
 *   <li>Otherwise treat the input as a model name. We exact-match
 *       against {@code mdm.model.name} / {@code display_name} first
 *       (cheaper, deterministic). On miss we fall back to a contains
 *       match ({@code ILIKE '%...%'}). When multiple devices share the
 *       same model row we collapse to the active one. The lookup also
 *       requires {@code device.is_active = TRUE}.</li>
 * </ol>
 *
 * <p>Why JDBC rather than JPA: the device entity has no model-name
 * column directly (it FKs to {@code mdm.model}), and we want a 4-column
 * projection — {@code (sku, grade.name, brand.name, model.name)} — not
 * a fully-hydrated {@code Device} entity tree. JPA would force lazy-load
 * navigation; the raw join is simpler and faster.
 */
@Service
public class ResolveReceivedDeviceService {

    /**
     * Schema: {@code mdm.device.sku} → ecoatm_code (the canonical
     * per-SKU identifier used by the bid_data pipeline);
     * {@code mdm.grade.name} → merged_grade (the same string stored on
     * {@code auctions.bid_data.merged_grade}).
     *
     * <p>Brand + grade + model are LEFT JOINs because not every device
     * row in dev/test carries every FK. The recommendation engine
     * tolerates null brand/grade (will recommend Decline as the safe
     * default).
     */
    private static final String EXACT_MODEL_NAME_SQL = """
            SELECT d.sku        AS ecoatm_code,
                   g.name       AS merged_grade,
                   b.name       AS brand,
                   m.name       AS model
            FROM mdm.device d
            LEFT JOIN mdm.brand  b ON d.brand_id  = b.id
            LEFT JOIN mdm.grade  g ON d.grade_id  = g.id
            LEFT JOIN mdm.model  m ON d.model_id  = m.id
            WHERE d.is_active = TRUE
              AND (LOWER(m.name) = LOWER(?) OR LOWER(m.display_name) = LOWER(?))
            ORDER BY d.id ASC
            LIMIT 1
            """;

    private static final String CONTAINS_MODEL_NAME_SQL = """
            SELECT d.sku        AS ecoatm_code,
                   g.name       AS merged_grade,
                   b.name       AS brand,
                   m.name       AS model
            FROM mdm.device d
            LEFT JOIN mdm.brand  b ON d.brand_id  = b.id
            LEFT JOIN mdm.grade  g ON d.grade_id  = g.id
            LEFT JOIN mdm.model  m ON d.model_id  = m.id
            WHERE d.is_active = TRUE
              AND (m.name ILIKE ? OR m.display_name ILIKE ?)
            ORDER BY d.id ASC
            LIMIT 1
            """;

    private final JdbcTemplate jdbc;

    public ResolveReceivedDeviceService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Resolves a buyer-entered IMEI or model-name string to its
     * canonical device tuple. Never throws; returns {@code empty} when
     * input is blank, IMEI-shaped (Phase 1), or no device matches.
     */
    public Optional<DeviceMatch> resolve(String imeiOrModel) {
        if (imeiOrModel == null) {
            return Optional.empty();
        }
        String trimmed = imeiOrModel.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }
        if (looksLikeImei(trimmed)) {
            // Phase 1: no IMEI lookup table on mdm.device. Phase 2 will
            // introduce a TAC → device mapping.
            return Optional.empty();
        }
        return findByModelExact(trimmed)
                .or(() -> findByModelContains(trimmed));
    }

    private Optional<DeviceMatch> findByModelExact(String name) {
        return queryOne(EXACT_MODEL_NAME_SQL, name, name);
    }

    private Optional<DeviceMatch> findByModelContains(String name) {
        String like = "%" + name + "%";
        return queryOne(CONTAINS_MODEL_NAME_SQL, like, like);
    }

    private Optional<DeviceMatch> queryOne(String sql, Object... args) {
        try {
            List<DeviceMatch> matches = jdbc.query(sql, (rs, rowNum) -> new DeviceMatch(
                    rs.getString("ecoatm_code"),
                    rs.getString("merged_grade"),
                    rs.getString("brand"),
                    rs.getString("model")
            ), args);
            return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private static boolean looksLikeImei(String s) {
        if (s.length() != 15) return false;
        for (int i = 0; i < 15; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Canonical device tuple. {@code ecoatmCode} is {@code mdm.device.sku};
     * {@code mergedGrade} is {@code mdm.grade.name} — both match the
     * shapes used by {@code auctions.bid_data} so the credit-calc
     * pipeline can use them directly.
     */
    public record DeviceMatch(String ecoatmCode, String mergedGrade,
                              String brand, String model) {}
}
