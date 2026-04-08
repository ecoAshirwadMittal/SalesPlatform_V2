package com.ecoatm.salesplatform.dto;

import java.util.ArrayList;
import java.util.List;

public class SubmitResponse {

    private boolean success;
    private boolean requiresSalesReview;
    private boolean alreadySubmitted;
    private List<String> validationErrors = new ArrayList<>();
    private List<String> exceedingItemSkus = new ArrayList<>();
    private int belowListPriceCount;
    private Long offerId;
    private String offerNumber;
    private String orderNumber;
    private String title;
    private String message;

    /** Validation failed (empty cart, generic errors). */
    public static SubmitResponse error(List<String> errors) {
        SubmitResponse r = new SubmitResponse();
        r.success = false;
        r.validationErrors = errors;
        r.title = "Submit order failed";
        r.message = errors.isEmpty() ? "Submission failed" : errors.get(0);
        return r;
    }

    /** Cart was already submitted in another window/tab. */
    public static SubmitResponse alreadySubmitted() {
        SubmitResponse r = new SubmitResponse();
        r.success = false;
        r.alreadySubmitted = true;
        r.title = "Cart Submitted!";
        r.message = "The cart you are attempting to submit has already been submitted in another window.";
        return r;
    }

    /** Items exceed available ATP quantity — return the offending SKUs. */
    public static SubmitResponse exceedingQty(Long offerId, List<String> skus) {
        SubmitResponse r = new SubmitResponse();
        r.success = false;
        r.offerId = offerId;
        r.exceedingItemSkus = skus;
        r.title = "Quantity Exceeded";
        r.message = skus.size() + " item(s) exceed available quantity. Please adjust quantities.";
        return r;
    }

    /** Items below list price — needs sales review. */
    public static SubmitResponse salesReview(Long offerId, int belowListPriceCount) {
        SubmitResponse r = new SubmitResponse();
        r.success = true;
        r.requiresSalesReview = true;
        r.offerId = offerId;
        r.belowListPriceCount = belowListPriceCount;
        r.title = "Almost Done!";
        r.message = belowListPriceCount == 1
                ? belowListPriceCount + " SKU will need to be reviewed by our sales team."
                : belowListPriceCount + " SKUs will need to be reviewed by our sales team.";
        return r;
    }

    /** Direct order submission succeeded. */
    public static SubmitResponse submitted(Long offerId, String orderNumber) {
        SubmitResponse r = new SubmitResponse();
        r.success = true;
        r.requiresSalesReview = false;
        r.offerId = offerId;
        r.orderNumber = orderNumber;
        r.title = "Thank you for your order!";
        r.message = "Order Number: " + orderNumber;
        return r;
    }

    /** Offer submitted for sales review (from "Almost Done" modal). */
    public static SubmitResponse offerSubmitted(Long offerId, String offerNumber) {
        SubmitResponse r = new SubmitResponse();
        r.success = true;
        r.offerId = offerId;
        r.offerNumber = offerNumber;
        r.title = "Offer submitted";
        r.message = "Your offer has been submitted, offer number: " + offerNumber;
        return r;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public boolean isRequiresSalesReview() { return requiresSalesReview; }
    public boolean isAlreadySubmitted() { return alreadySubmitted; }
    public List<String> getValidationErrors() { return validationErrors; }
    public List<String> getExceedingItemSkus() { return exceedingItemSkus; }
    public int getBelowListPriceCount() { return belowListPriceCount; }
    public Long getOfferId() { return offerId; }
    public String getOfferNumber() { return offerNumber; }
    public String getOrderNumber() { return orderNumber; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
}
