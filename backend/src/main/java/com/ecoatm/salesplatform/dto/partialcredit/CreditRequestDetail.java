package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Full detail shape returned from {@code GET .../{id}}. Carries the
 * header + each line list. Photos are returned separately by the photo
 * endpoint (Sprint 4 work — placeholder ints today).
 */
public record CreditRequestDetail(
        Long id,
        String requestNumber,
        String orderNumber,
        // V91 (Figma parity fix #2): contact name distinct from
        // partyName (company). Nullable on legacy rows.
        String buyerName,
        String partyName,
        Instant orderCreatedDate,
        Instant orderShippedDate,
        SystemStatus systemStatus,
        String displayStatus,
        // Figma parity fix #3 + #4 — Sprint 3 §11.Q2 wants the admin
        // detail to show the *internal* status text; buyer detail keeps
        // the external label (already in displayStatus). statusColorHex
        // flows live from credit_request_statuses for the pill colour.
        String internalStatusText,
        String statusColorHex,
        ShipmentDamaged shipmentDamaged,
        boolean hasMissingDevice,
        boolean hasWrongDevice,
        boolean hasEncumberedDevice,
        Integer totalDevices,
        BigDecimal requestedTotal,
        BigDecimal approvedTotal,
        // Sprint 4 chunk 5 — buyer detail surfaces both fields so it can
        // render the "Reviewed on …" panel only after the request is
        // finalised. Both are null until completeReview() runs.
        Long reviewedById,
        Instant reviewCompletedOn,
        List<MissingDeviceLineDto> missingLines,
        List<WrongDeviceLineDto> wrongLines,
        List<EncumberedDeviceLineDto> encumberedLines) {

    /**
     * Full builder used by admin + buyer detail controllers post-V91.
     * Caller resolves the status meta (internal text + colour hex) +
     * the per-line photo counts in bulk before invoking this.
     */
    public static CreditRequestDetail from(
            CreditRequest cr,
            SystemStatus systemStatus,
            String displayStatus,
            String internalStatusText,
            String statusColorHex,
            List<MissingDeviceLine> missingLines,
            List<WrongDeviceLine> wrongLines,
            List<EncumberedDeviceLine> encumberedLines,
            java.util.Map<Long, Integer> wrongPhotoCountsByLineId) {
        return new CreditRequestDetail(
                cr.getId(),
                cr.getRequestNumber(),
                cr.getOrderNumber(),
                cr.getBuyerName(),
                cr.getPartyName(),
                cr.getOrderCreatedDate(),
                cr.getOrderShippedDate(),
                systemStatus,
                displayStatus,
                internalStatusText,
                statusColorHex,
                cr.getShipmentDamaged(),
                Boolean.TRUE.equals(cr.getHasMissingDevice()),
                Boolean.TRUE.equals(cr.getHasWrongDevice()),
                Boolean.TRUE.equals(cr.getHasEncumberedDevice()),
                cr.getTotalDevices(),
                cr.getRequestedTotal(),
                cr.getApprovedTotal(),
                cr.getReviewedById(),
                cr.getReviewCompletedOn(),
                missingLines.stream().map(MissingDeviceLineDto::from).toList(),
                wrongLines.stream()
                        .map(line -> WrongDeviceLineDto.from(
                                line,
                                wrongPhotoCountsByLineId == null
                                        ? 0
                                        : wrongPhotoCountsByLineId.getOrDefault(line.getId(), 0)))
                        .toList(),
                encumberedLines.stream().map(EncumberedDeviceLineDto::from).toList());
    }

    /**
     * Backwards-compatible overload — defaults internal text to display
     * text, photo counts to empty, and colour hex to null. Used by
     * call sites that pre-date V91 (status config, email-templates page,
     * older test fixtures). New code should call the full overload.
     */
    public static CreditRequestDetail from(
            CreditRequest cr,
            SystemStatus systemStatus,
            String displayStatus,
            List<MissingDeviceLine> missingLines,
            List<WrongDeviceLine> wrongLines,
            List<EncumberedDeviceLine> encumberedLines) {
        return from(cr, systemStatus, displayStatus, displayStatus, null,
                missingLines, wrongLines, encumberedLines, java.util.Map.of());
    }

    public record MissingDeviceLineDto(
            Long id,
            String barcodeSubmitted,
            String brand,
            String model,
            String grade,
            String boxNumber,
            BigDecimal amountPaid,
            String shipStatus,
            String lineStatus,
            String reviewDecision,
            BigDecimal amountToCredit) {

        public static MissingDeviceLineDto from(MissingDeviceLine line) {
            return new MissingDeviceLineDto(
                    line.getId(),
                    line.getBarcodeSubmitted(),
                    line.getBrand(),
                    line.getModel(),
                    line.getGrade(),
                    line.getBoxNumber(),
                    line.getAmountPaid(),
                    line.getShipStatus() == null ? null : line.getShipStatus().name(),
                    line.getLineStatus() == null ? null : line.getLineStatus().name(),
                    line.getReviewDecision() == null ? null : line.getReviewDecision().name(),
                    line.getAmountToCredit());
        }
    }

    public record WrongDeviceLineDto(
            Long id,
            String expectedBarcode,
            // Figma parity fix #5: Box No. column on admin Wrong table.
            // Already a column on the JPA entity (expected_box_number);
            // this field surfaces it through the DTO so the admin UI
            // stops rendering "—" placeholders.
            String expectedBoxNumber,
            String expectedBrand,
            String expectedModel,
            String expectedGrade,
            BigDecimal expectedAmountPaid,
            String actualImeiOrModel,
            // Figma parity fix #6a: alias of actualImeiOrModel — buyer
            // detail labels this column "Received Device IMEI/Serial".
            // Same underlying field today; Phase 2 may split.
            String receivedImei,
            String actualBrand,
            String actualModel,
            String actualGrade,
            BigDecimal latestPrice,
            String actionRecommendation,
            String lineStatus,
            String reviewDecision,
            BigDecimal amountToCredit,
            // Figma parity fix #6b: per-line photo count for the buyer
            // detail Wrong table's Photos column. Resolved in bulk by
            // the controller before invoking the parent from() builder.
            Integer photoCount) {

        /** Used by the V91 controller path that pre-resolved photo counts. */
        public static WrongDeviceLineDto from(WrongDeviceLine line, int photoCount) {
            return new WrongDeviceLineDto(
                    line.getId(),
                    line.getExpectedBarcode(),
                    line.getExpectedBoxNumber(),
                    line.getExpectedBrand(),
                    line.getExpectedModel(),
                    line.getExpectedGrade(),
                    line.getExpectedAmountPaid(),
                    line.getActualImeiOrModel(),
                    line.getActualImeiOrModel(),
                    line.getActualBrand(),
                    line.getActualModel(),
                    line.getActualGrade(),
                    line.getLatestPrice(),
                    line.getActionRecommendation() == null ? null : line.getActionRecommendation().name(),
                    line.getLineStatus() == null ? null : line.getLineStatus().name(),
                    line.getReviewDecision() == null ? null : line.getReviewDecision().name(),
                    line.getAmountToCredit(),
                    photoCount);
        }

        /** Backwards-compatible overload — defaults photoCount to 0. */
        public static WrongDeviceLineDto from(WrongDeviceLine line) {
            return from(line, 0);
        }
    }

    public record EncumberedDeviceLineDto(
            Long id,
            String barcodeSubmitted,
            String brand,
            String model,
            String grade,
            String boxNumber,
            BigDecimal amountPaid,
            String prologResult,
            BigDecimal actualValue,
            String lineStatus,
            String reviewDecision,
            BigDecimal amountToCredit) {

        public static EncumberedDeviceLineDto from(EncumberedDeviceLine line) {
            return new EncumberedDeviceLineDto(
                    line.getId(),
                    line.getBarcodeSubmitted(),
                    line.getBrand(),
                    line.getModel(),
                    line.getGrade(),
                    line.getBoxNumber(),
                    line.getAmountPaid(),
                    line.getPrologResult() == null ? null : line.getPrologResult().name(),
                    line.getActualValue(),
                    line.getLineStatus() == null ? null : line.getLineStatus().name(),
                    line.getReviewDecision() == null ? null : line.getReviewDecision().name(),
                    line.getAmountToCredit());
        }
    }
}
