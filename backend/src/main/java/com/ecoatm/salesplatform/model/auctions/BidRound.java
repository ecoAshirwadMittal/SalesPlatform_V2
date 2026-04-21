package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA mapping of {@code auctions.bid_rounds}.
 *
 * <p>Direct columns: id, scheduling_auction_id, submitted, submitted_datetime,
 * submitted_by_user_id. Round timing + status (start_datetime, end_datetime,
 * round_status) live on the parent {@code scheduling_auctions} row — the
 * delegating getters expose them through a lazy {@link SchedulingAuction}
 * association so callers can treat round timing as a property of the round.
 */
@Entity
@Table(name = "bid_rounds", schema = "auctions")
public class BidRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduling_auction_id", nullable = false)
    private Long schedulingAuctionId;

    @Column(name = "submitted", nullable = false)
    private boolean submitted = false;

    @Column(name = "submitted_datetime")
    private Instant submittedDatetime;

    @Column(name = "submitted_by_user_id")
    private Long submittedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduling_auction_id", insertable = false, updatable = false)
    private SchedulingAuction schedulingAuction;

    public Long getId() {
        return id;
    }

    public Long getSchedulingAuctionId() {
        return schedulingAuctionId;
    }

    public void setSchedulingAuctionId(Long schedulingAuctionId) {
        this.schedulingAuctionId = schedulingAuctionId;
    }

    public boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public Instant getSubmittedDatetime() {
        return submittedDatetime;
    }

    public void setSubmittedDatetime(Instant submittedDatetime) {
        this.submittedDatetime = submittedDatetime;
    }

    public Long getSubmittedByUserId() {
        return submittedByUserId;
    }

    public void setSubmittedByUserId(Long submittedByUserId) {
        this.submittedByUserId = submittedByUserId;
    }

    /** Delegates to the parent SchedulingAuction. Returns the enum name as a String. */
    public String getRoundStatus() {
        return schedulingAuction == null || schedulingAuction.getRoundStatus() == null
                ? null
                : schedulingAuction.getRoundStatus().name();
    }

    /** Delegates to the parent SchedulingAuction. */
    public Instant getStartDatetime() {
        return schedulingAuction == null ? null : schedulingAuction.getStartDatetime();
    }

    /** Delegates to the parent SchedulingAuction. */
    public Instant getEndDatetime() {
        return schedulingAuction == null ? null : schedulingAuction.getEndDatetime();
    }
}
