package com.ecoatm.salesplatform.dto;

import java.util.List;

public class RmaSubmitResponse {

    private boolean success;
    private String rmaNumber;
    private Long rmaId;
    private int itemCount;
    private String message;
    private List<String> errors;

    public RmaSubmitResponse() {}

    public static RmaSubmitResponse success(Long rmaId, String rmaNumber, int itemCount) {
        RmaSubmitResponse r = new RmaSubmitResponse();
        r.success = true;
        r.rmaId = rmaId;
        r.rmaNumber = rmaNumber;
        r.itemCount = itemCount;
        r.message = "RMA " + rmaNumber + " submitted successfully with " + itemCount + " item(s).";
        return r;
    }

    public static RmaSubmitResponse failure(List<String> errors) {
        RmaSubmitResponse r = new RmaSubmitResponse();
        r.success = false;
        r.errors = errors;
        r.message = "RMA submission failed with " + errors.size() + " validation error(s).";
        return r;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getRmaNumber() { return rmaNumber; }
    public void setRmaNumber(String rmaNumber) { this.rmaNumber = rmaNumber; }

    public Long getRmaId() { return rmaId; }
    public void setRmaId(Long rmaId) { this.rmaId = rmaId; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
