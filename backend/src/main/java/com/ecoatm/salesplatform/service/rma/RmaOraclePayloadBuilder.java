package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the Oracle "create RMA" JSON payload for a reviewed RMA.
 *
 * <p>Mendix parity: {@code SUB_RMA_PrepareOraclePayload} — creates an
 * {@code RMARequest} header ({@code OriginSystemOrderId} = RMA number,
 * {@code OrderType = 'PWS-RMA'}, {@code OrderDate} = {@code yyyyMMddHHmmss},
 * {@code BuyerCode}, {@code OriginSystemUser} = the reviewer) plus one
 * {@code RMALineItem} per <b>approved</b> item ({@code ItemNumber} = device
 * SKU, {@code UnitSellingPrice}, {@code RMAOriginalOrder} = order number,
 * {@code RMAIMEI}, {@code RMAReason}).
 *
 * <p>Structure mirrors the order path ({@code OfferService.prepareOraclePayload}):
 * camelCase keys wrapped in a top-level {@code {"request": {...}}} envelope,
 * prices serialised as strings. A pure function of its inputs — the caller
 * ({@code RmaOracleService}) resolves the approved-item list, device SKUs,
 * buyer code and reviewer name; this class only serialises them.
 *
 * <p><b>Payload-shape assumption (documented per the Task B0 brief):</b> the
 * Mendix export mapping's exact JSON key casing is not recoverable from
 * {@code migration_context/} (only the entity attribute names are), so the keys
 * here are a faithful best-effort in the order path's camelCase convention.
 * Local/dev runs simulate the Oracle call (the payload is never actually
 * transmitted), so any casing correction needed for the real QA/prod Oracle
 * contract is a follow-up that does not affect this task's behavior.
 */
@Component
public class RmaOraclePayloadBuilder {

    private static final String ORDER_TYPE_RMA = "PWS-RMA";
    private static final DateTimeFormatter ORDER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ObjectMapper objectMapper;

    public RmaOraclePayloadBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Serialise the Oracle create-RMA request.
     *
     * @param rma             the reviewed RMA (supplies {@code number})
     * @param approvedItems   the RMA items that were approved (one payload line
     *                        each); already filtered by the caller
     * @param skuByDeviceId   device-id → SKU lookup for the {@code itemNumber}
     *                        field; a missing entry renders an empty string
     * @param buyerCode       the buyer code string (payload {@code buyerCode})
     * @param originSystemUser the reviewer name (payload {@code originSystemUser})
     * @return the JSON payload string
     */
    public String build(Rma rma,
                        List<RmaItem> approvedItems,
                        Map<Long, String> skuByDeviceId,
                        String buyerCode,
                        String originSystemUser) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("originSystemOrderId", nullToEmpty(rma.getNumber()));
        request.put("orderType", ORDER_TYPE_RMA);
        request.put("orderDate", LocalDateTime.now().format(ORDER_DATE_FORMAT));
        request.put("buyerCode", nullToEmpty(buyerCode));
        request.put("originSystemUser", nullToEmpty(originSystemUser));

        List<Map<String, Object>> lines = new ArrayList<>();
        for (RmaItem item : approvedItems) {
            Map<String, Object> line = new LinkedHashMap<>();
            String sku = item.getDeviceId() != null ? skuByDeviceId.get(item.getDeviceId()) : null;
            line.put("itemNumber", nullToEmpty(sku));
            line.put("unitSellingPrice", priceToString(item.getSalePrice()));
            line.put("rmaOriginalOrder", nullToEmpty(item.getOrderNumber()));
            line.put("rmaImei", nullToEmpty(item.getImei()));
            line.put("rmaReason", nullToEmpty(item.getReturnReason()));
            lines.add(line);
        }
        request.put("rmaLineItem", lines);

        try {
            return objectMapper.writeValueAsString(Map.of("request", request));
        } catch (JsonProcessingException e) {
            // Matches OfferService.prepareOraclePayload: a serialization failure
            // is a programming error, surfaced so the caller routes to failure.
            throw new IllegalStateException("Failed to serialize Oracle RMA payload", e);
        }
    }

    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private static String priceToString(BigDecimal price) {
        return price != null ? price.toPlainString() : "";
    }
}
