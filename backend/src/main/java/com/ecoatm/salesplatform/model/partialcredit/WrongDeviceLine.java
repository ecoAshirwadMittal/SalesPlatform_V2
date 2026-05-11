package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ActionRecommendation;
import com.ecoatm.salesplatform.model.partialcredit.enums.LineRowStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Wrong-device line — buyer reports a device different from what the
 * manifest says shipped. Expected* fields come from Snowflake at submit
 * time; Actual* fields come from the buyer's wizard entries; latest_price
 * is the live aggregate of max submitted bid for the received device
 * within the same auction week (cached at review-open).
 */
@Entity
@Table(name = "wrong_device_lines", schema = "partial_credit")
public class WrongDeviceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_request_id", nullable = false) private Long creditRequestId;

    @Column(name = "expected_barcode", nullable = false, length = 200) private String expectedBarcode;
    @Column(name = "expected_device_description", length = 400)         private String expectedDeviceDescription;
    @Column(name = "expected_brand", length = 100)                      private String expectedBrand;
    @Column(name = "expected_model", length = 200)                      private String expectedModel;
    @Column(name = "expected_grade", length = 40)                       private String expectedGrade;
    @Column(name = "expected_box_number", length = 100)                 private String expectedBoxNumber;
    @Column(name = "expected_amount_paid", precision = 14, scale = 2)   private BigDecimal expectedAmountPaid;
    @Column(name = "expected_ecoatm_code", length = 40)                 private String expectedEcoatmCode;
    @Column(name = "expected_week_id")                                  private Long expectedWeekId;

    @Column(name = "actual_imei_or_model", length = 200)        private String actualImeiOrModel;
    @Column(name = "actual_device_description", length = 400)    private String actualDeviceDescription;
    @Column(name = "actual_ecoatm_code", length = 40)            private String actualEcoatmCode;
    @Column(name = "actual_grade", length = 40)                  private String actualGrade;
    @Column(name = "actual_brand", length = 100)                 private String actualBrand;
    @Column(name = "actual_model", length = 200)                 private String actualModel;

    @Column(name = "latest_price", precision = 14, scale = 4)    private BigDecimal latestPrice;
    @Column(name = "latest_price_computed_on")                   private Instant latestPriceComputedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_recommendation", length = 20)
    private ActionRecommendation actionRecommendation;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_status", nullable = false, length = 20)
    private LineRowStatus lineStatus = LineRowStatus.VALID;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_decision", nullable = false, length = 20)
    private ReviewDecision reviewDecision = ReviewDecision.PENDING;

    @Column(name = "amount_to_credit", precision = 14, scale = 2)
    private BigDecimal amountToCredit;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "created_by_id") private Long createdById;
    @Column(name = "changed_by_id") private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreditRequestId() { return creditRequestId; }
    public void setCreditRequestId(Long v) { this.creditRequestId = v; }
    public String getExpectedBarcode() { return expectedBarcode; }
    public void setExpectedBarcode(String v) { this.expectedBarcode = v; }
    public String getExpectedDeviceDescription() { return expectedDeviceDescription; }
    public void setExpectedDeviceDescription(String v) { this.expectedDeviceDescription = v; }
    public String getExpectedBrand() { return expectedBrand; }
    public void setExpectedBrand(String v) { this.expectedBrand = v; }
    public String getExpectedModel() { return expectedModel; }
    public void setExpectedModel(String v) { this.expectedModel = v; }
    public String getExpectedGrade() { return expectedGrade; }
    public void setExpectedGrade(String v) { this.expectedGrade = v; }
    public String getExpectedBoxNumber() { return expectedBoxNumber; }
    public void setExpectedBoxNumber(String v) { this.expectedBoxNumber = v; }
    public BigDecimal getExpectedAmountPaid() { return expectedAmountPaid; }
    public void setExpectedAmountPaid(BigDecimal v) { this.expectedAmountPaid = v; }
    public String getExpectedEcoatmCode() { return expectedEcoatmCode; }
    public void setExpectedEcoatmCode(String v) { this.expectedEcoatmCode = v; }
    public Long getExpectedWeekId() { return expectedWeekId; }
    public void setExpectedWeekId(Long v) { this.expectedWeekId = v; }
    public String getActualImeiOrModel() { return actualImeiOrModel; }
    public void setActualImeiOrModel(String v) { this.actualImeiOrModel = v; }
    public String getActualDeviceDescription() { return actualDeviceDescription; }
    public void setActualDeviceDescription(String v) { this.actualDeviceDescription = v; }
    public String getActualEcoatmCode() { return actualEcoatmCode; }
    public void setActualEcoatmCode(String v) { this.actualEcoatmCode = v; }
    public String getActualGrade() { return actualGrade; }
    public void setActualGrade(String v) { this.actualGrade = v; }
    public String getActualBrand() { return actualBrand; }
    public void setActualBrand(String v) { this.actualBrand = v; }
    public String getActualModel() { return actualModel; }
    public void setActualModel(String v) { this.actualModel = v; }
    public BigDecimal getLatestPrice() { return latestPrice; }
    public void setLatestPrice(BigDecimal v) { this.latestPrice = v; }
    public Instant getLatestPriceComputedOn() { return latestPriceComputedOn; }
    public void setLatestPriceComputedOn(Instant v) { this.latestPriceComputedOn = v; }
    public ActionRecommendation getActionRecommendation() { return actionRecommendation; }
    public void setActionRecommendation(ActionRecommendation v) { this.actionRecommendation = v; }
    public LineRowStatus getLineStatus() { return lineStatus; }
    public void setLineStatus(LineRowStatus v) { this.lineStatus = v; }
    public ReviewDecision getReviewDecision() { return reviewDecision; }
    public void setReviewDecision(ReviewDecision v) { this.reviewDecision = v; }
    public BigDecimal getAmountToCredit() { return amountToCredit; }
    public void setAmountToCredit(BigDecimal v) { this.amountToCredit = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
