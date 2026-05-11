package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.UploadKind;
import com.ecoatm.salesplatform.model.partialcredit.enums.UploadProcessStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "credit_request_uploads", schema = "partial_credit")
public class CreditRequestUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_request_id", nullable = false) private Long creditRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UploadKind kind;

    @Column(name = "original_filename", nullable = false, length = 400) private String originalFilename;
    @Column(name = "content_type", nullable = false, length = 100)      private String contentType;
    @Column(name = "size_bytes", nullable = false)                       private Integer sizeBytes;

    @Lob
    @Column(nullable = false)
    private byte[] blob;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false, length = 20)
    private UploadProcessStatus processStatus = UploadProcessStatus.PENDING;

    @Column(name = "error_report", columnDefinition = "TEXT") private String errorReport;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "created_by_id") private Long createdById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreditRequestId() { return creditRequestId; }
    public void setCreditRequestId(Long v) { this.creditRequestId = v; }
    public UploadKind getKind() { return kind; }
    public void setKind(UploadKind v) { this.kind = v; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String v) { this.originalFilename = v; }
    public String getContentType() { return contentType; }
    public void setContentType(String v) { this.contentType = v; }
    public Integer getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Integer v) { this.sizeBytes = v; }
    public byte[] getBlob() { return blob; }
    public void setBlob(byte[] v) { this.blob = v; }
    public UploadProcessStatus getProcessStatus() { return processStatus; }
    public void setProcessStatus(UploadProcessStatus v) { this.processStatus = v; }
    public String getErrorReport() { return errorReport; }
    public void setErrorReport(String v) { this.errorReport = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById = v; }
}
