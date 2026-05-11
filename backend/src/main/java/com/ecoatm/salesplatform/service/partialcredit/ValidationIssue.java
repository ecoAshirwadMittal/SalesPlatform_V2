package com.ecoatm.salesplatform.service.partialcredit;

/**
 * One thing wrong with a credit request at submit time. The {@code code}
 * is the stable key the frontend keys off (driver of which form field to
 * highlight); the {@code message} is the human-readable copy that surfaces
 * in the toast or inline error. Codes mirror the rules listed in
 * {@code partial-credit-implementation-plan.md} §7 so the docs stay
 * checkable against the implementation.
 */
public record ValidationIssue(String code, String message) {

    public static ValidationIssue orderNotFound(String orderNumber) {
        return new ValidationIssue(
                "ORDER_NOT_FOUND",
                "Order " + orderNumber + " is not on the manifest for your buyer code.");
    }

    public static ValidationIssue orderOutsideWindow(int windowDays) {
        return new ValidationIssue(
                "ORDER_OUTSIDE_WINDOW",
                "Credit requests must be submitted within " + windowDays
                        + " days of the order's shipped date.");
    }

    public static ValidationIssue orderHasActiveRequest() {
        return new ValidationIssue(
                "ORDER_HAS_ACTIVE_REQUEST",
                "There is already an active credit request for this order. "
                        + "Wait until it is reviewed before submitting another.");
    }

    public static ValidationIssue noReasonSelected() {
        return new ValidationIssue(
                "NO_REASON_SELECTED",
                "Select at least one reason (Missing Device, Wrong Device, or Encumbered Device).");
    }

    public static ValidationIssue damageNotAnswered() {
        return new ValidationIssue(
                "DAMAGE_NOT_ANSWERED",
                "Tell us whether the shipment was damaged.");
    }

    public static ValidationIssue damageRequiresPhoto() {
        return new ValidationIssue(
                "DAMAGE_REQUIRES_PHOTO",
                "Attach at least one damage photo when you mark the shipment as damaged.");
    }

    public static ValidationIssue missingDeviceLineMissing() {
        return new ValidationIssue(
                "MISSING_DEVICE_LINES_REQUIRED",
                "Add at least one barcode to the Missing Devices step.");
    }

    public static ValidationIssue wrongDeviceLineMissing() {
        return new ValidationIssue(
                "WRONG_DEVICE_LINES_REQUIRED",
                "Add at least one entry to the Wrong Devices step.");
    }

    public static ValidationIssue encumberedDeviceLineMissing() {
        return new ValidationIssue(
                "ENCUMBERED_DEVICE_LINES_REQUIRED",
                "Add at least one barcode to the Encumbered Devices step.");
    }
}
