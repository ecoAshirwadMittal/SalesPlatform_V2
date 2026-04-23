package com.ecoatm.salesplatform.model.admin;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persistent record of an uploaded Buyer User Guide PDF.
 *
 * <p>At most one row may have {@code isActive = true} at a time, enforced by
 * the partial unique index {@code uq_buyer_user_guide_active} in V74.
 *
 * <p>{@code isDeleted} is a soft-delete flag; active rows cannot be deleted.
 */
@Entity
@Table(name = "buyer_user_guide", schema = "admin")
public class BuyerUserGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    // Relative path under backend/uploads/buyer-user-guide/
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "byte_size", nullable = false)
    private long byteSize;

    @Column(name = "uploaded_by", nullable = false)
    private long uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Getters

    public Long getId() { return id; }

    public String getFileName() { return fileName; }

    public String getFilePath() { return filePath; }

    public String getContentType() { return contentType; }

    public long getByteSize() { return byteSize; }

    public long getUploadedBy() { return uploadedBy; }

    public Instant getUploadedAt() { return uploadedAt; }

    public boolean isActive() { return isActive; }

    public boolean isDeleted() { return isDeleted; }

    // Setters

    public void setFileName(String fileName) { this.fileName = fileName; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public void setByteSize(long byteSize) { this.byteSize = byteSize; }

    public void setUploadedBy(long uploadedBy) { this.uploadedBy = uploadedBy; }

    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }

    public void setActive(boolean active) { isActive = active; }

    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}
