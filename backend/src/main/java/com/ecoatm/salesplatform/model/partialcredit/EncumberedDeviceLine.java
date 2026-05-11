package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.LineRowStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.PrologResult;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Encumbered-device line. Phase 1 reviewer enters {@code prologResult}
 * and {@code actualValue} manually; Phase 2 will automate the Prolog
 * check and wire {@code rmaId} to an auto-created RMA record.
 */
@Entity
@Table(name = "encumbered_device_lines", schema = "partial_credit")
public class EncumberedDeviceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_request_id", nullable = false) private Long creditRequestId;
    @Column(name = "barcode_submitted", nullable = false, length = 200) private String barcodeSubmitted;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_status", nullable = false, length = 20)
    private LineRowStatus lineStatus = LineRowStatus.VALID;

    @Column(length = 100) private String brand;
    @Column(length = 200) private String model;
    @Column(length = 40)  private String grade;
    @Column(name = "box_number", length = 100)             private String boxNumber;
    @Column(name = "amount_paid", precision = 14, scale = 2) private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "prolog_result", nullable = false, length = 20)
    private PrologResult prologResult = PrologResult.PENDING;

    @Column(name = "actual_value", precision = 14, scale = 2) private BigDecimal actualValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_decision", nullable = false, length = 20)
    private ReviewDecision reviewDecision = ReviewDecision.PENDING;

    @Column(name = "amount_to_credit", precision = 14, scale = 2) private BigDecimal amountToCredit;

    @Column(name = "rma_id") private Long rmaId;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "created_by_id") private Long createdById;
    @Column(name = "changed_by_id") private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreditRequestId() { return creditRequestId; }
    public void setCreditRequestId(Long v) { this.creditRequestId = v; }
    public String getBarcodeSubmitted() { return barcodeSubmitted; }
    public void setBarcodeSubmitted(String v) { this.barcodeSubmitted = v; }
    public LineRowStatus getLineStatus() { return lineStatus; }
    public void setLineStatus(LineRowStatus v) { this.lineStatus = v; }
    public String getBrand() { return brand; }
    public void setBrand(String v) { this.brand = v; }
    public String getModel() { return model; }
    public void setModel(String v) { this.model = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String v) { this.boxNumber = v; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal v) { this.amountPaid = v; }
    public PrologResult getPrologResult() { return prologResult; }
    public void setPrologResult(PrologResult v) { this.prologResult = v; }
    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal v) { this.actualValue = v; }
    public ReviewDecision getReviewDecision() { return reviewDecision; }
    public void setReviewDecision(ReviewDecision v) { this.reviewDecision = v; }
    public BigDecimal getAmountToCredit() { return amountToCredit; }
    public void setAmountToCredit(BigDecimal v) { this.amountToCredit = v; }
    public Long getRmaId() { return rmaId; }
    public void setRmaId(Long v) { this.rmaId = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
