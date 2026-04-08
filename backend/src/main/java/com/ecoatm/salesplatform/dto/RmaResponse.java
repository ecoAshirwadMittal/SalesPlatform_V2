package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.Rma;

import java.time.LocalDateTime;

public class RmaResponse {

    private Long rmaId;
    private String number;
    private String systemStatus;
    private String internalStatusText;
    private String externalStatusText;
    private String statusGroupedTo;
    private Long buyerCodeId;
    private String buyerName;
    private String companyName;
    private Integer requestSkus;
    private Integer requestQty;
    private Integer requestSalesTotal;
    private Integer approvedSkus;
    private Integer approvedQty;
    private Integer approvedSalesTotal;
    private Integer approvedCount;
    private Integer declinedCount;
    private LocalDateTime submittedDate;
    private LocalDateTime approvalDate;
    private LocalDateTime reviewCompletedOn;
    private String oracleNumber;

    public static RmaResponse fromEntity(Rma rma) {
        RmaResponse r = new RmaResponse();
        r.rmaId = rma.getId();
        r.number = rma.getNumber();
        r.buyerCodeId = rma.getBuyerCodeId();
        r.requestSkus = rma.getRequestSkus() != null ? rma.getRequestSkus() : 0;
        r.requestQty = rma.getRequestQty() != null ? rma.getRequestQty() : 0;
        r.requestSalesTotal = rma.getRequestSalesTotal() != null ? rma.getRequestSalesTotal() : 0;
        r.approvedSkus = rma.getApprovedSkus() != null ? rma.getApprovedSkus() : 0;
        r.approvedQty = rma.getApprovedQty() != null ? rma.getApprovedQty() : 0;
        r.approvedSalesTotal = rma.getApprovedSalesTotal() != null ? rma.getApprovedSalesTotal() : 0;
        r.approvedCount = rma.getApprovedCount() != null ? rma.getApprovedCount() : 0;
        r.declinedCount = rma.getDeclinedCount() != null ? rma.getDeclinedCount() : 0;
        r.submittedDate = rma.getSubmittedDate();
        r.approvalDate = rma.getApprovalDate();
        r.reviewCompletedOn = rma.getReviewCompletedOn();
        r.oracleNumber = rma.getOracleNumber();
        if (rma.getRmaStatus() != null) {
            r.systemStatus = rma.getRmaStatus().getSystemStatus();
            r.internalStatusText = rma.getRmaStatus().getInternalStatusText();
            r.externalStatusText = rma.getRmaStatus().getExternalStatusText();
            r.statusGroupedTo = rma.getRmaStatus().getStatusGroupedTo();
        }
        return r;
    }

    // Getters
    public Long getRmaId() { return rmaId; }
    public String getNumber() { return number; }
    public String getSystemStatus() { return systemStatus; }
    public String getInternalStatusText() { return internalStatusText; }
    public String getExternalStatusText() { return externalStatusText; }
    public String getStatusGroupedTo() { return statusGroupedTo; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public String getBuyerName() { return buyerName; }
    public String getCompanyName() { return companyName; }
    public Integer getRequestSkus() { return requestSkus; }
    public Integer getRequestQty() { return requestQty; }
    public Integer getRequestSalesTotal() { return requestSalesTotal; }
    public Integer getApprovedSkus() { return approvedSkus; }
    public Integer getApprovedQty() { return approvedQty; }
    public Integer getApprovedSalesTotal() { return approvedSalesTotal; }
    public Integer getApprovedCount() { return approvedCount; }
    public Integer getDeclinedCount() { return declinedCount; }
    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public LocalDateTime getReviewCompletedOn() { return reviewCompletedOn; }
    public String getOracleNumber() { return oracleNumber; }

    // Setters for buyer/company enrichment
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
