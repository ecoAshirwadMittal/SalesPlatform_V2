package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "scheduling_auctions", schema = "auctions")
public class SchedulingAuction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "auction_id", nullable = false)
    private Long auctionId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "round", nullable = false)
    private int round;

    @Column(name = "auction_week_year", length = 50)
    private String auctionWeekYear;

    @Column(name = "start_datetime")
    private Instant startDatetime;

    @Column(name = "end_datetime")
    private Instant endDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_status", length = 20, nullable = false)
    private SchedulingAuctionStatus roundStatus = SchedulingAuctionStatus.Unscheduled;

    @Enumerated(EnumType.STRING)
    @Column(name = "round3_init_status", length = 20, nullable = false)
    private ScheduleAuctionInitStatus round3InitStatus = ScheduleAuctionInitStatus.Pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_reminders", length = 20, nullable = false)
    private ReminderEmails emailReminders = ReminderEmails.NoneSent;

    @Column(name = "has_round", nullable = false)
    private boolean hasRound = true;

    @Column(name = "is_start_notification_sent", nullable = false)
    private boolean startNotificationSent;

    @Column(name = "is_end_notification_sent", nullable = false)
    private boolean endNotificationSent;

    @Column(name = "is_reminder_notification_sent", nullable = false)
    private boolean reminderNotificationSent;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;

    @Column(name = "snowflake_json")
    private String snowflakeJson;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    @Column(name = "updated_by", length = 200)
    private String updatedBy;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;

    public Long getId() { return id; }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public String getAuctionWeekYear() { return auctionWeekYear; }
    public void setAuctionWeekYear(String auctionWeekYear) { this.auctionWeekYear = auctionWeekYear; }

    public Instant getStartDatetime() { return startDatetime; }
    public void setStartDatetime(Instant startDatetime) { this.startDatetime = startDatetime; }

    public Instant getEndDatetime() { return endDatetime; }
    public void setEndDatetime(Instant endDatetime) { this.endDatetime = endDatetime; }

    public SchedulingAuctionStatus getRoundStatus() { return roundStatus; }
    public void setRoundStatus(SchedulingAuctionStatus roundStatus) { this.roundStatus = roundStatus; }

    public ScheduleAuctionInitStatus getRound3InitStatus() { return round3InitStatus; }
    public void setRound3InitStatus(ScheduleAuctionInitStatus round3InitStatus) {
        this.round3InitStatus = round3InitStatus;
    }

    public ReminderEmails getEmailReminders() { return emailReminders; }
    public void setEmailReminders(ReminderEmails emailReminders) { this.emailReminders = emailReminders; }

    public boolean isHasRound() { return hasRound; }
    public void setHasRound(boolean hasRound) { this.hasRound = hasRound; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }
}
