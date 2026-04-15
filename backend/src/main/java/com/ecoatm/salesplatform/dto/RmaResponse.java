package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.Rma;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
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
    private BigDecimal requestSalesTotal;
    private Integer approvedSkus;
    private Integer approvedQty;
    private BigDecimal approvedSalesTotal;
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
        r.requestSalesTotal = rma.getRequestSalesTotal() != null ? rma.getRequestSalesTotal() : BigDecimal.ZERO;
        r.approvedSkus = rma.getApprovedSkus() != null ? rma.getApprovedSkus() : 0;
        r.approvedQty = rma.getApprovedQty() != null ? rma.getApprovedQty() : 0;
        r.approvedSalesTotal = rma.getApprovedSalesTotal() != null ? rma.getApprovedSalesTotal() : BigDecimal.ZERO;
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
}
