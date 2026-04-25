package com.ecoatm.salesplatform.model.auctions;

import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.model.mdm.Week;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order", schema = "auctions")
public class PurchaseOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_from_id")
    private Week weekFrom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_to_id")
    private Week weekTo;

    @Column(name = "week_range_label", nullable = false, length = 200)
    private String weekRangeLabel;

    @Column(name = "valid_year_week", nullable = false)
    private Boolean validYearWeek = Boolean.TRUE;

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords = 0;

    @Column(name = "po_refresh_timestamp")
    private Instant poRefreshTimestamp;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PODetail> details = new ArrayList<>();

    @PreUpdate void onUpdate() { this.changedDate = Instant.now(); }

    public Long getId() { return id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Week getWeekFrom() { return weekFrom; }
    public void setWeekFrom(Week v) { this.weekFrom = v; }
    public Week getWeekTo() { return weekTo; }
    public void setWeekTo(Week v) { this.weekTo = v; }
    public String getWeekRangeLabel() { return weekRangeLabel; }
    public void setWeekRangeLabel(String v) { this.weekRangeLabel = v; }
    public Boolean getValidYearWeek() { return validYearWeek; }
    public void setValidYearWeek(Boolean v) { this.validYearWeek = v; }
    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer v) { this.totalRecords = v; }
    public Instant getPoRefreshTimestamp() { return poRefreshTimestamp; }
    public void setPoRefreshTimestamp(Instant v) { this.poRefreshTimestamp = v; }
    public Instant getCreatedDate() { return createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public User getOwner() { return owner; }
    public void setOwner(User v) { this.owner = v; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User v) { this.changedBy = v; }
    public List<PODetail> getDetails() { return details; }
}
