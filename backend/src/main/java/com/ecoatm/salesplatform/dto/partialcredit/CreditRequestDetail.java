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
        String partyName,
        Instant orderCreatedDate,
        Instant orderShippedDate,
        SystemStatus systemStatus,
        String displayStatus,
        ShipmentDamaged shipmentDamaged,
        boolean hasMissingDevice,
        boolean hasWrongDevice,
        boolean hasEncumberedDevice,
        Integer totalDevices,
        BigDecimal requestedTotal,
        List<MissingDeviceLineDto> missingLines,
        List<WrongDeviceLineDto> wrongLines,
        List<EncumberedDeviceLineDto> encumberedLines) {

    public static CreditRequestDetail from(
            CreditRequest cr,
            SystemStatus systemStatus,
            String displayStatus,
            List<MissingDeviceLine> missingLines,
            List<WrongDeviceLine> wrongLines,
            List<EncumberedDeviceLine> encumberedLines) {
        return new CreditRequestDetail(
                cr.getId(),
                cr.getRequestNumber(),
                cr.getOrderNumber(),
                cr.getPartyName(),
                cr.getOrderCreatedDate(),
                cr.getOrderShippedDate(),
                systemStatus,
                displayStatus,
                cr.getShipmentDamaged(),
                Boolean.TRUE.equals(cr.getHasMissingDevice()),
                Boolean.TRUE.equals(cr.getHasWrongDevice()),
                Boolean.TRUE.equals(cr.getHasEncumberedDevice()),
                cr.getTotalDevices(),
                cr.getRequestedTotal(),
                missingLines.stream().map(MissingDeviceLineDto::from).toList(),
                wrongLines.stream().map(WrongDeviceLineDto::from).toList(),
                encumberedLines.stream().map(EncumberedDeviceLineDto::from).toList());
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
            String expectedBrand,
            String expectedModel,
            String expectedGrade,
            BigDecimal expectedAmountPaid,
            String actualImeiOrModel,
            String actualBrand,
            String actualModel,
            String actualGrade,
            BigDecimal latestPrice,
            String actionRecommendation,
            String lineStatus,
            String reviewDecision,
            BigDecimal amountToCredit) {

        public static WrongDeviceLineDto from(WrongDeviceLine line) {
            return new WrongDeviceLineDto(
                    line.getId(),
                    line.getExpectedBarcode(),
                    line.getExpectedBrand(),
                    line.getExpectedModel(),
                    line.getExpectedGrade(),
                    line.getExpectedAmountPaid(),
                    line.getActualImeiOrModel(),
                    line.getActualBrand(),
                    line.getActualModel(),
                    line.getActualGrade(),
                    line.getLatestPrice(),
                    line.getActionRecommendation() == null ? null : line.getActionRecommendation().name(),
                    line.getLineStatus() == null ? null : line.getLineStatus().name(),
                    line.getReviewDecision() == null ? null : line.getReviewDecision().name(),
                    line.getAmountToCredit());
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
