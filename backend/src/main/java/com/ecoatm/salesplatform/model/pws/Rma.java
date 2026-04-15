package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rma", schema = "pws")
public class Rma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "submitted_by_user_id")
    private Long submittedByUserId;

    @Column(name = "reviewed_by_user_id")
    private Long reviewedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rma_status_id")
    private RmaStatus rmaStatus;

    @Column(name = "request_skus")
    private Integer requestSkus;

    @Column(name = "request_qty")
    private Integer requestQty;

    @Column(name = "request_sales_total", precision = 14, scale = 2)
    private BigDecimal requestSalesTotal;

    @Column(name = "approved_skus")
    private Integer approvedSkus;

    @Column(name = "approved_qty")
    private Integer approvedQty;

    @Column(name = "approved_sales_total", precision = 14, scale = 2)
    private BigDecimal approvedSalesTotal;

    @Column(name = "approved_count")
    private Integer approvedCount;

    @Column(name = "declined_count")
    private Integer declinedCount;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "review_completed_on")
    private LocalDateTime reviewCompletedOn;

    @Column(name = "system_status")
    private String systemStatus;

    @Column(name = "oracle_rma_status")
    private String oracleRmaStatus;

    @Column(name = "oracle_number")
    private String oracleNumber;

    @Column(name = "oracle_id")
    private String oracleId;

    @Column(name = "oracle_http_code")
    private Integer oracleHttpCode;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "all_rma_items_valid")
    private Boolean allRmaItemsValid;

    @Column(name = "json_content")
    private String jsonContent;

    @Column(name = "oracle_json_response")
    private String oracleJsonResponse;

    @Column(name = "entity_owner")
    private String entityOwner;

    @Column(name = "entity_changer")
    private String entityChanger;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "rma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RmaItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }

    public Long getSubmittedByUserId() { return submittedByUserId; }
    public void setSubmittedByUserId(Long submittedByUserId) { this.submittedByUserId = submittedByUserId; }

    public Long getReviewedByUserId() { return reviewedByUserId; }
    public void setReviewedByUserId(Long reviewedByUserId) { this.reviewedByUserId = reviewedByUserId; }

    public RmaStatus getRmaStatus() { return rmaStatus; }
    public void setRmaStatus(RmaStatus rmaStatus) { this.rmaStatus = rmaStatus; }

    public Integer getRequestSkus() { return requestSkus; }
    public void setRequestSkus(Integer requestSkus) { this.requestSkus = requestSkus; }

    public Integer getRequestQty() { return requestQty; }
    public void setRequestQty(Integer requestQty) { this.requestQty = requestQty; }

    public BigDecimal getRequestSalesTotal() { return requestSalesTotal; }
    public void setRequestSalesTotal(BigDecimal requestSalesTotal) { this.requestSalesTotal = requestSalesTotal; }

    public Integer getApprovedSkus() { return approvedSkus; }
    public void setApprovedSkus(Integer approvedSkus) { this.approvedSkus = approvedSkus; }

    public Integer getApprovedQty() { return approvedQty; }
    public void setApprovedQty(Integer approvedQty) { this.approvedQty = approvedQty; }

    public BigDecimal getApprovedSalesTotal() { return approvedSalesTotal; }
    public void setApprovedSalesTotal(BigDecimal approvedSalesTotal) { this.approvedSalesTotal = approvedSalesTotal; }

    public Integer getApprovedCount() { return approvedCount; }
    public void setApprovedCount(Integer approvedCount) { this.approvedCount = approvedCount; }

    public Integer getDeclinedCount() { return declinedCount; }
    public void setDeclinedCount(Integer declinedCount) { this.declinedCount = declinedCount; }

    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public LocalDateTime getReviewCompletedOn() { return reviewCompletedOn; }
    public void setReviewCompletedOn(LocalDateTime reviewCompletedOn) { this.reviewCompletedOn = reviewCompletedOn; }

    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }

    public String getOracleRmaStatus() { return oracleRmaStatus; }
    public void setOracleRmaStatus(String oracleRmaStatus) { this.oracleRmaStatus = oracleRmaStatus; }

    public String getOracleNumber() { return oracleNumber; }
    public void setOracleNumber(String oracleNumber) { this.oracleNumber = oracleNumber; }

    public String getOracleId() { return oracleId; }
    public void setOracleId(String oracleId) { this.oracleId = oracleId; }

    public Integer getOracleHttpCode() { return oracleHttpCode; }
    public void setOracleHttpCode(Integer oracleHttpCode) { this.oracleHttpCode = oracleHttpCode; }

    public Boolean getIsSuccessful() { return isSuccessful; }
    public void setIsSuccessful(Boolean isSuccessful) { this.isSuccessful = isSuccessful; }

    public Boolean getAllRmaItemsValid() { return allRmaItemsValid; }
    public void setAllRmaItemsValid(Boolean allRmaItemsValid) { this.allRmaItemsValid = allRmaItemsValid; }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }

    public String getOracleJsonResponse() { return oracleJsonResponse; }
    public void setOracleJsonResponse(String oracleJsonResponse) { this.oracleJsonResponse = oracleJsonResponse; }

    public String getEntityOwner() { return entityOwner; }
    public void setEntityOwner(String entityOwner) { this.entityOwner = entityOwner; }

    public String getEntityChanger() { return entityChanger; }
    public void setEntityChanger(String entityChanger) { this.entityChanger = entityChanger; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public List<RmaItem> getItems() { return items; }
    public void setItems(List<RmaItem> items) { this.items = items; }
}
