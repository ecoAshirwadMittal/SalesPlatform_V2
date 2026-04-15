package com.ecoatm.salesplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Unified admin controller for PWS Control Center sub-pages.
 * Each endpoint serves CRUD for the corresponding admin config table.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class PWSAdminController {

    private final JdbcTemplate jdbc;

    // ── Feature Flags ──

    @GetMapping("/feature-flags")
    public List<Map<String, Object>> listFeatureFlags() {
        return jdbc.queryForList("SELECT id, name, active, description, created_date, updated_date FROM pws.feature_flag ORDER BY name");
    }

    @PutMapping("/feature-flags/{id}")
    public ResponseEntity<Map<String, Object>> updateFeatureFlag(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.feature_flag SET name=?, active=?, description=?, updated_date=? WHERE id=?",
                body.get("name"), body.get("active"), body.get("description"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/feature-flags")
    public ResponseEntity<Map<String, Object>> createFeatureFlag(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO pws.feature_flag (name, active, description) VALUES (?, ?, ?)",
                body.get("name"), body.get("active"), body.get("description"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/feature-flags/{id}")
    public ResponseEntity<Map<String, Object>> deleteFeatureFlag(@PathVariable Long id) {
        jdbc.update("DELETE FROM pws.feature_flag WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── PWS Error Messages ──

    @GetMapping("/error-messages")
    public List<Map<String, Object>> listErrorMessages() {
        return jdbc.queryForList("SELECT id, source_system, source_error_code, source_error_type, user_error_code, user_error_message, bypass_for_user FROM integration.error_mapping ORDER BY source_system, source_error_code");
    }

    @PutMapping("/error-messages/{id}")
    public ResponseEntity<Map<String, Object>> updateErrorMessage(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE integration.error_mapping SET source_system=?, source_error_code=?, source_error_type=?, user_error_code=?, user_error_message=?, bypass_for_user=? WHERE id=?",
                body.get("sourceSystem"), body.get("sourceErrorCode"), body.get("sourceErrorType"),
                body.get("userErrorCode"), body.get("userErrorMessage"), body.get("bypassForUser"), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/error-messages")
    public ResponseEntity<Map<String, Object>> createErrorMessage(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO integration.error_mapping (source_system, source_error_code, source_error_type, user_error_code, user_error_message, bypass_for_user) VALUES (?, ?, ?, ?, ?, ?)",
                body.get("sourceSystem"), body.get("sourceErrorCode"), body.get("sourceErrorType"),
                body.get("userErrorCode"), body.get("userErrorMessage"), body.get("bypassForUser"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/error-messages/{id}")
    public ResponseEntity<Map<String, Object>> deleteErrorMessage(@PathVariable Long id) {
        jdbc.update("DELETE FROM integration.error_mapping WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Ranks Configuration (Brand) ──

    @GetMapping("/ranks-config")
    public List<Map<String, Object>> listRanksConfig() {
        return jdbc.queryForList("SELECT id, name, display_name, is_enabled, sort_rank FROM mdm.brand ORDER BY sort_rank");
    }

    @PutMapping("/ranks-config/{id}")
    public ResponseEntity<Map<String, Object>> updateRanksConfig(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE mdm.brand SET name=?, display_name=?, is_enabled=?, sort_rank=? WHERE id=?",
                body.get("name"), body.get("displayName"), body.get("isEnabled"), body.get("sortRank"), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── PWS Constants ──

    @GetMapping("/pws-constants")
    public Map<String, Object> getPWSConstants() {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM pws.pws_constants LIMIT 1");
        if (rows.isEmpty()) {
            return jdbc.queryForList(
                    "INSERT INTO pws.pws_constants (sla_days, send_first_reminder, send_second_reminder) VALUES (2, true, true) RETURNING *"
            ).get(0);
        }
        return rows.get(0);
    }

    @PutMapping("/pws-constants")
    public ResponseEntity<Map<String, Object>> updatePWSConstants(@RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.pws_constants SET sla_days=?, sales_email=?, send_first_reminder=?, send_second_reminder=?, hours_first_counter_reminder=?, hours_second_counter_reminder=?, updated_date=?",
                body.get("slaDays"), body.get("salesEmail"), body.get("sendFirstReminder"), body.get("sendSecondReminder"),
                body.get("hoursFirstCounterReminder"), body.get("hoursSecondCounterReminder"), LocalDateTime.now());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Order Status Config ──

    @GetMapping("/order-status")
    public List<Map<String, Object>> listOrderStatus() {
        return jdbc.queryForList("SELECT * FROM pws.order_status_config ORDER BY id");
    }

    @PutMapping("/order-status/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.order_status_config SET system_status=?, internal_status_text=?, external_status_text=?, internal_css_class=?, external_css_class=?, description=?, updated_date=? WHERE id=?",
                body.get("systemStatus"), body.get("internalStatusText"), body.get("externalStatusText"),
                body.get("internalCssClass"), body.get("externalCssClass"), body.get("description"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/order-status")
    public ResponseEntity<Map<String, Object>> createOrderStatus(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO pws.order_status_config (system_status, internal_status_text, external_status_text, internal_css_class, external_css_class, description) VALUES (?, ?, ?, ?, ?, ?)",
                body.get("systemStatus"), body.get("internalStatusText"), body.get("externalStatusText"),
                body.get("internalCssClass"), body.get("externalCssClass"), body.get("description"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/order-status/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrderStatus(@PathVariable Long id) {
        jdbc.update("DELETE FROM pws.order_status_config WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Maintenance Mode ──

    @GetMapping("/maintenance-mode")
    public Map<String, Object> getMaintenanceMode() {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM pws.maintenance_mode LIMIT 1");
        if (rows.isEmpty()) {
            return jdbc.queryForList(
                    "INSERT INTO pws.maintenance_mode (is_enabled) VALUES (false) RETURNING *"
            ).get(0);
        }
        return rows.get(0);
    }

    @PutMapping("/maintenance-mode")
    public ResponseEntity<Map<String, Object>> updateMaintenanceMode(@RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.maintenance_mode SET is_enabled=?, banner_start_time=?::timestamp, start_time=?::timestamp, end_time=?::timestamp, banner_title=?, banner_message=?, page_title=?, page_header=?, page_message=?, updated_date=?",
                body.get("isEnabled"), body.get("bannerStartTime"), body.get("startTime"), body.get("endTime"),
                body.get("bannerTitle"), body.get("bannerMessage"), body.get("pageTitle"), body.get("pageHeader"),
                body.get("pageMessage"), LocalDateTime.now());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── RMA Status ──

    @GetMapping("/rma-status")
    public List<Map<String, Object>> listRmaStatus() {
        return jdbc.queryForList("SELECT * FROM pws.rma_status ORDER BY sort_order");
    }

    @PutMapping("/rma-status/{id}")
    public ResponseEntity<Map<String, Object>> updateRmaStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.rma_status SET sort_order=?, system_status=?, internal_status_text=?, external_status_text=?, status_group=?, bidder_message=?, description=?, is_default=?, updated_date=? WHERE id=?",
                body.get("sortOrder"), body.get("systemStatus"), body.get("internalStatusText"), body.get("externalStatusText"),
                body.get("statusGroup"), body.get("bidderMessage"), body.get("description"), body.get("isDefault"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/rma-status")
    public ResponseEntity<Map<String, Object>> createRmaStatus(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO pws.rma_status (sort_order, system_status, internal_status_text, external_status_text, status_group, bidder_message, description, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                body.get("sortOrder"), body.get("systemStatus"), body.get("internalStatusText"), body.get("externalStatusText"),
                body.get("statusGroup"), body.get("bidderMessage"), body.get("description"), body.get("isDefault"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/rma-status/{id}")
    public ResponseEntity<Map<String, Object>> deleteRmaStatus(@PathVariable Long id) {
        jdbc.update("DELETE FROM pws.rma_status WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── RMA Template + Reasons ──

    @GetMapping("/rma-templates")
    public Map<String, Object> listRmaTemplates() {
        List<Map<String, Object>> templates = jdbc.queryForList("SELECT * FROM pws.rma_template ORDER BY id");
        List<Map<String, Object>> reasons = jdbc.queryForList("SELECT * FROM pws.rma_reason ORDER BY id");
        return Map.of("templates", templates, "reasons", reasons);
    }

    @PutMapping("/rma-templates/{id}")
    public ResponseEntity<Map<String, Object>> updateRmaTemplate(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.rma_template SET template_name=?, is_active=?, file_name=?, updated_date=? WHERE id=?",
                body.get("templateName"), body.get("isActive"), body.get("fileName"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/rma-reasons/{id}")
    public ResponseEntity<Map<String, Object>> updateRmaReason(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.rma_reason SET valid_reason=?, is_active=?, updated_date=? WHERE id=?",
                body.get("validReason"), body.get("isActive"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/rma-reasons")
    public ResponseEntity<Map<String, Object>> createRmaReason(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO pws.rma_reason (valid_reason, is_active) VALUES (?, ?)",
                body.get("validReason"), body.get("isActive"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/rma-reasons/{id}")
    public ResponseEntity<Map<String, Object>> deleteRmaReason(@PathVariable Long id) {
        jdbc.update("DELETE FROM pws.rma_reason WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Navigation Menu ──

    @GetMapping("/navigation-menu")
    public List<Map<String, Object>> listNavigationMenu() {
        return jdbc.queryForList("SELECT * FROM pws.navigation_menu ORDER BY user_group, sort_order");
    }

    @PutMapping("/navigation-menu/{id}")
    public ResponseEntity<Map<String, Object>> updateNavigationMenu(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE pws.navigation_menu SET name=?, is_active=?, sort_order=?, loading_message=?, microflow_name=?, icon_css_class=?, user_group=?, updated_date=? WHERE id=?",
                body.get("name"), body.get("isActive"), body.get("sortOrder"), body.get("loadingMessage"),
                body.get("microflowName"), body.get("iconCssClass"), body.get("userGroup"), LocalDateTime.now(), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/navigation-menu")
    public ResponseEntity<Map<String, Object>> createNavigationMenu(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO pws.navigation_menu (name, is_active, sort_order, loading_message, microflow_name, icon_css_class, user_group) VALUES (?, ?, ?, ?, ?, ?, ?)",
                body.get("name"), body.get("isActive"), body.get("sortOrder"), body.get("loadingMessage"),
                body.get("microflowName"), body.get("iconCssClass"), body.get("userGroup"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/navigation-menu/{id}")
    public ResponseEntity<Map<String, Object>> deleteNavigationMenu(@PathVariable Long id) {
        jdbc.update("DELETE FROM pws.navigation_menu WHERE id=?", id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Deposco Config ──

    @GetMapping("/deposco")
    public Map<String, Object> getDeposcoConfig() {
        List<Map<String, Object>> configs = jdbc.queryForList("SELECT id, base_url, timeout_ms, is_active, last_sync_time, updated_date FROM integration.deposco_config LIMIT 1");
        return Map.of("config", configs.isEmpty() ? Map.of() : configs.get(0));
    }

    @PutMapping("/deposco")
    public ResponseEntity<Map<String, Object>> updateDeposcoConfig(@RequestBody Map<String, Object> body) {
        List<Map<String, Object>> existing = jdbc.queryForList("SELECT id FROM integration.deposco_config LIMIT 1");
        if (existing.isEmpty()) {
            jdbc.update("INSERT INTO integration.deposco_config (base_url, timeout_ms, is_active) VALUES (?, ?, ?)",
                    body.get("baseUrl"), body.get("timeoutMs"), body.get("isActive"));
        } else {
            Long id = ((Number) existing.get(0).get("id")).longValue();
            jdbc.update("UPDATE integration.deposco_config SET base_url=?, timeout_ms=?, is_active=?, updated_date=? WHERE id=?",
                    body.get("baseUrl"), body.get("timeoutMs"), body.get("isActive"), LocalDateTime.now(), id);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── SLA Tags Actions ──

    @PostMapping("/sla-tags/set")
    public ResponseEntity<Map<String, Object>> setSLATags() {
        int updated = jdbc.update(
                "UPDATE pws.offer SET offer_beyond_sla = true, updated_date = NOW() " +
                "WHERE status IN ('Sales_Review', 'SALES_REVIEW', 'Buyer_Acceptance', 'BUYER_ACCEPTANCE') " +
                "AND updated_date < NOW() - INTERVAL '2 days' AND (offer_beyond_sla IS NULL OR offer_beyond_sla = false)");
        return ResponseEntity.ok(Map.of("success", true, "message", "SLA tags set on " + updated + " overdue offer(s)."));
    }

    @PostMapping("/sla-tags/remove")
    public ResponseEntity<Map<String, Object>> removeSLATags() {
        int updated = jdbc.update(
                "UPDATE pws.offer SET offer_beyond_sla = false, updated_date = NOW() " +
                "WHERE status IN ('Sales_Review', 'SALES_REVIEW', 'Buyer_Acceptance', 'BUYER_ACCEPTANCE') " +
                "AND offer_beyond_sla = true");
        return ResponseEntity.ok(Map.of("success", true, "message", "SLA tags removed from " + updated + " offer(s)."));
    }

    // ── Snowflake Sync Action ──

    @PostMapping("/snowflake/sync")
    public ResponseEntity<Map<String, Object>> syncSnowflake() {
        // TODO: Implement actual Snowflake sync
        return ResponseEntity.ok(Map.of("success", true, "message", "Snowflake sync triggered. (Stubbed — not yet connected to Snowflake)"));
    }
}
