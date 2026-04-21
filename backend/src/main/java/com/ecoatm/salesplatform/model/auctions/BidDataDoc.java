package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bid_data_docs", schema = "auctions")
public class BidDataDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Column(name = "week_id", nullable = false)
    private Long weekId;

    @Column(name = "file_name", length = 500)
    private String fileName;

    @Column(name = "file_ref", length = 1000)
    private String fileRef;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 200)
    private String contentType;

    @Column(name = "uploaded_datetime")
    private Instant uploadedDatetime;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate;

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }
    public Long getWeekId() { return weekId; }
    public void setWeekId(Long weekId) { this.weekId = weekId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileRef() { return fileRef; }
    public void setFileRef(String fileRef) { this.fileRef = fileRef; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Instant getUploadedDatetime() { return uploadedDatetime; }
    public void setUploadedDatetime(Instant uploadedDatetime) { this.uploadedDatetime = uploadedDatetime; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BidDataDoc other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
