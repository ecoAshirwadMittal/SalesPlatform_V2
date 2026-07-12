package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link RmaOraclePayloadBuilder} — the Oracle create-RMA JSON
 * shape, mirroring the Mendix {@code SUB_RMA_PrepareOraclePayload} field map
 * (header {@code OriginSystemOrderId} / {@code OrderType='PWS-RMA'} /
 * {@code OrderDate} / {@code BuyerCode} / {@code OriginSystemUser}; per-line
 * {@code ItemNumber} / {@code UnitSellingPrice} / {@code RMAOriginalOrder} /
 * {@code RMAIMEI} / {@code RMAReason}). Only APPROVED lines are included.
 */
class RmaOraclePayloadBuilderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RmaOraclePayloadBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new RmaOraclePayloadBuilder(objectMapper);
    }

    private RmaItem item(String imei, Long deviceId, BigDecimal price, String orderNumber,
                         String reason, String status) {
        RmaItem it = new RmaItem();
        it.setImei(imei);
        it.setDeviceId(deviceId);
        it.setSalePrice(price);
        it.setOrderNumber(orderNumber);
        it.setReturnReason(reason);
        it.setStatus(status);
        return it;
    }

    @Test
    @DisplayName("builds header + one line per approved item, wrapped in {\"request\":{...}}")
    void buildsHeaderAndApprovedLines() throws Exception {
        Rma rma = new Rma();
        rma.setNumber("RMABC00126001");

        RmaItem approved = item("IMEI-1", 10L, new BigDecimal("175.00"), "ORD-1",
                "Physically Damaged", "Approve");

        String json = builder.build(
                rma,
                List.of(approved),
                Map.of(10L, "SKU-10"),
                "BC001",
                "sales.rep");

        JsonNode root = objectMapper.readTree(json);
        JsonNode req = root.get("request");
        assertThat(req).isNotNull();
        assertThat(req.get("originSystemOrderId").asText()).isEqualTo("RMABC00126001");
        assertThat(req.get("orderType").asText()).isEqualTo("PWS-RMA");
        assertThat(req.get("orderDate").asText()).matches("\\d{14}"); // yyyyMMddHHmmss
        assertThat(req.get("buyerCode").asText()).isEqualTo("BC001");
        assertThat(req.get("originSystemUser").asText()).isEqualTo("sales.rep");

        JsonNode lines = req.get("rmaLineItem");
        assertThat(lines).isNotNull();
        assertThat(lines).hasSize(1);
        JsonNode line = lines.get(0);
        assertThat(line.get("itemNumber").asText()).isEqualTo("SKU-10");
        assertThat(line.get("unitSellingPrice").asText()).isEqualTo("175.00");
        assertThat(line.get("rmaOriginalOrder").asText()).isEqualTo("ORD-1");
        assertThat(line.get("rmaImei").asText()).isEqualTo("IMEI-1");
        assertThat(line.get("rmaReason").asText()).isEqualTo("Physically Damaged");
    }

    @Test
    @DisplayName("empty approved-item list yields an empty rmaLineItem array (no lines)")
    void noApprovedItems_emptyLineArray() throws Exception {
        Rma rma = new Rma();
        rma.setNumber("RMABC00126002");

        String json = builder.build(rma, List.of(), Map.of(), "BC001", "sales.rep");

        JsonNode lines = objectMapper.readTree(json).get("request").get("rmaLineItem");
        assertThat(lines).isNotNull();
        assertThat(lines).isEmpty();
    }

    @Test
    @DisplayName("null sale price / missing SKU render as empty strings, not the literal 'null'")
    void nullFields_renderAsEmptyStrings() throws Exception {
        Rma rma = new Rma();
        rma.setNumber("RMABC00126003");
        RmaItem it = item("IMEI-9", 99L, null, null, "Defective", "Approve");

        // deviceId 99 not present in the SKU map → itemNumber falls back to blank
        String json = builder.build(rma, List.of(it), Map.of(), "BC001", "sales.rep");

        JsonNode line = objectMapper.readTree(json).get("request").get("rmaLineItem").get(0);
        assertThat(line.get("itemNumber").asText()).isEmpty();
        assertThat(line.get("unitSellingPrice").asText()).isEmpty();
        assertThat(line.get("rmaOriginalOrder").asText()).isEmpty();
    }
}
