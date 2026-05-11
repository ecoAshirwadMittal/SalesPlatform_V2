package com.ecoatm.salesplatform.model.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "credit_request_photos", schema = "partial_credit")
public class CreditRequestPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_request_id", nullable = false) private Long creditRequestId;
    @Column(name = "wrong_device_line_id")                private Long wrongDeviceLineId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhotoKind kind;

    @Column(name = "original_filename", nullable = false, length = 400) private String originalFilename;
    @Column(name = "content_type", nullable = false, length = 100)      private String contentType;
    @Column(name = "size_bytes", nullable = false)                       private Integer sizeBytes;

    @Lob
    @Column(nullable = false)
    private byte[] blob;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "created_by_id") private Long createdById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreditRequestId() { return creditRequestId; }
    public void setCreditRequestId(Long v) { this.creditRequestId = v; }
    public Long getWrongDeviceLineId() { return wrongDeviceLineId; }
    public void setWrongDeviceLineId(Long v) { this.wrongDeviceLineId = v; }
    public PhotoKind getKind() { return kind; }
    public void setKind(PhotoKind v) { this.kind = v; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String v) { this.originalFilename = v; }
    public String getContentType() { return contentType; }
    public void setContentType(String v) { this.contentType = v; }
    public Integer getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Integer v) { this.sizeBytes = v; }
    public byte[] getBlob() { return blob; }
    public void setBlob(byte[] v) { this.blob = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById = v; }
}
