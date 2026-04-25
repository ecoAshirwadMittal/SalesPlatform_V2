package com.ecoatm.salesplatform.model.mdm;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "week", schema = "mdm")
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "week_id")
    private Long weekId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "week_start_datetime")
    private Instant weekStartDateTime;

    @Column(name = "week_end_datetime")
    private Instant weekEndDateTime;

    @Column(name = "week_display")
    private String weekDisplay;

    @Column(name = "week_display_short")
    private String weekDisplayShort;

    @Column(name = "auction_data_purged")
    private boolean auctionDataPurged;

    public Long getId() { return id; }
    public Long getWeekId() { return weekId; }
    public Integer getYear() { return year; }
    public Integer getWeekNumber() { return weekNumber; }
    public Instant getWeekStartDateTime() { return weekStartDateTime; }
    public Instant getWeekEndDateTime() { return weekEndDateTime; }
    public String getWeekDisplay() { return weekDisplay; }
    public String getWeekDisplayShort() { return weekDisplayShort; }
    public boolean isAuctionDataPurged() { return auctionDataPurged; }

    // Setters added for test fixtures (PO module, sub-project 4B).
    // Production code never mutates Week — rows are loaded by Flyway seed
    // and via direct repository persistence with constructor-built objects.
    public void setWeekStartDateTime(Instant v) { this.weekStartDateTime = v; }
    public void setWeekEndDateTime(Instant v) { this.weekEndDateTime = v; }
}
