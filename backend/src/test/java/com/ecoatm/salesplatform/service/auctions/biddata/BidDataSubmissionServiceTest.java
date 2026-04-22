package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidSubmissionResult;
import com.ecoatm.salesplatform.dto.SaveBidRequest;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link BidDataSubmissionService} — the save + submit engine.
 *
 * <p>The 8 scenarios mirror the Mendix decision tree from
 * {@code SUB_SaveBidData} / {@code ACT_SubmitBids}. Two are particularly
 * load-bearing: the resubmit "slide" semantics
 * (submitted_* → last_valid_*; bid_* → submitted_*) and the
 * single-buyer scope on the submit UPDATE (we add
 * {@code AND buyer_code_id = ?} to the spec WHERE — without it a submit
 * by user A would silently flip rows for buyer codes A doesn't own).
 */
@ExtendWith(MockitoExtension.class)
class BidDataSubmissionServiceTest {

    private static final long USER_ID = 7L;
    private static final long BID_DATA_ID = 100L;
    private static final long BUYER_CODE_ID = 55L;
    private static final long BID_ROUND_ID = 200L;
    private static final String USERNAME = "bidder@buyerco.com";

    @Mock
    private BidDataRepository bidDataRepo;

    @Mock
    private BidRoundRepository bidRoundRepo;

    @Mock
    private JdbcTemplate jdbc;

    private BidDataSubmissionService service;

    @BeforeEach
    void setUp() {
        service = new BidDataSubmissionService(bidDataRepo, bidRoundRepo, jdbc);
    }

    @AfterEach
    void clearSecurityContext() {
        // Tests that authenticate as Administrator must not leak into siblings.
        SecurityContextHolder.clearContext();
    }

    // -----------------------------------------------------------------------
    // save() scenarios
    // -----------------------------------------------------------------------

    @Test
    void save_validates_ownership() {
        BidData row = bidDataRow(5);
        when(bidDataRepo.findById(BID_DATA_ID)).thenReturn(Optional.of(row));
        // ownership query (2-hop JOIN) returns 0 → user does not own buyer code
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(0L);

        assertThatThrownBy(() ->
                service.save(USER_ID, BID_DATA_ID, new SaveBidRequest(5, new BigDecimal("10.00"))))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("NOT_YOUR_BID_DATA");

        verify(bidDataRepo, never()).save(any());
    }

    @Test
    void save_allowsNullBidQuantity() {
        BidData row = bidDataRow(5);
        when(bidDataRepo.findById(BID_DATA_ID)).thenReturn(Optional.of(row));
        // ownership passes
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        // round open
        when(jdbc.queryForObject(anyString(), eq(String.class), eq(BID_ROUND_ID)))
                .thenReturn("Started");
        when(bidDataRepo.save(any(BidData.class))).thenAnswer(inv -> inv.getArgument(0));

        BidDataRow result = service.save(USER_ID, BID_DATA_ID,
                new SaveBidRequest(null, new BigDecimal("10.00")));

        assertThat(result).isNotNull();
        assertThat(result.bidQuantity()).isNull();
        assertThat(result.bidAmount()).isEqualByComparingTo("10.00");
        verify(bidDataRepo).save(any(BidData.class));
    }

    @Test
    void save_rejectsNegativeQuantity() {
        BidData row = bidDataRow(5);
        when(bidDataRepo.findById(BID_DATA_ID)).thenReturn(Optional.of(row));
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        when(jdbc.queryForObject(anyString(), eq(String.class), eq(BID_ROUND_ID)))
                .thenReturn("Started");

        assertThatThrownBy(() ->
                service.save(USER_ID, BID_DATA_ID, new SaveBidRequest(-1, new BigDecimal("10.00"))))
                .isInstanceOf(BidDataValidationException.class)
                .extracting("code").isEqualTo("INVALID_QUANTITY");

        verify(bidDataRepo, never()).save(any());
    }

    @Test
    void save_rejectsWhenQuantityExceedsMax() {
        BidData row = bidDataRow(5);
        when(bidDataRepo.findById(BID_DATA_ID)).thenReturn(Optional.of(row));
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        when(jdbc.queryForObject(anyString(), eq(String.class), eq(BID_ROUND_ID)))
                .thenReturn("Started");

        assertThatThrownBy(() ->
                service.save(USER_ID, BID_DATA_ID, new SaveBidRequest(10, new BigDecimal("10.00"))))
                .isInstanceOf(BidDataValidationException.class)
                .extracting("code").isEqualTo("INVALID_QUANTITY");

        verify(bidDataRepo, never()).save(any());
    }

    @Test
    void save_rejectsWhenRoundClosed() {
        BidData row = bidDataRow(5);
        when(bidDataRepo.findById(BID_DATA_ID)).thenReturn(Optional.of(row));
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        when(jdbc.queryForObject(anyString(), eq(String.class), eq(BID_ROUND_ID)))
                .thenReturn("Closed");

        assertThatThrownBy(() ->
                service.save(USER_ID, BID_DATA_ID, new SaveBidRequest(5, new BigDecimal("10.00"))))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("ROUND_CLOSED");

        verify(bidDataRepo, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // submit() scenarios
    // -----------------------------------------------------------------------

    @Test
    void submit_firstSubmit_copiesBidToSubmittedWithNullLastValid() {
        // ownership passes
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        BidRound round = bidRound(false /* not previously submitted */, "Started");
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(round));
        when(jdbc.update(anyString(), eq(USERNAME), eq(USER_ID), eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(3);
        when(jdbc.update(anyString(), eq(USER_ID), eq(BID_ROUND_ID))).thenReturn(1);

        BidSubmissionResult result = service.submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.bidRoundId()).isEqualTo(BID_ROUND_ID);
        assertThat(result.rowCount()).isEqualTo(3);
        assertThat(result.resubmit()).isFalse();
        assertThat(result.submittedDatetime()).isNotNull();

        // capture the bid_data UPDATE SQL — guards against accidental WHERE-clause drift
        // that would let a submit by buyer A flip buyer B's rows.
        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbc).update(sql.capture(), eq(USERNAME), eq(USER_ID), eq(BID_ROUND_ID), eq(BUYER_CODE_ID));
        assertThat(sql.getValue())
                .contains("UPDATE auctions.bid_data")
                .contains("last_valid_bid_quantity = submitted_bid_quantity")
                .contains("last_valid_bid_amount   = submitted_bid_amount")
                .contains("submitted_bid_quantity  = bid_quantity")
                .contains("submitted_bid_amount    = bid_amount")
                .contains("bid_round_id = ?")
                .contains("buyer_code_id = ?");

        // also capture the bid_rounds UPDATE — flips submitted = TRUE
        ArgumentCaptor<String> roundSql = ArgumentCaptor.forClass(String.class);
        verify(jdbc).update(roundSql.capture(), eq(USER_ID), eq(BID_ROUND_ID));
        assertThat(roundSql.getValue())
                .contains("UPDATE auctions.bid_rounds")
                .contains("submitted            = TRUE")
                .contains("WHERE id = ?");
    }

    @Test
    void submit_resubmit_slidesSubmittedToLastValid() {
        // round.submitted = true → this is a resubmit. The UPDATE itself is the
        // same shape; what changes is the result.resubmit() flag and the
        // semantic that submitted_* on each row already has prior values which
        // the SQL slides into last_valid_* before overwriting with bid_*.
        // ownership passes
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        BidRound round = bidRound(true /* previously submitted */, "Started");
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(round));
        when(jdbc.update(anyString(), eq(USERNAME), eq(USER_ID), eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(3);
        when(jdbc.update(anyString(), eq(USER_ID), eq(BID_ROUND_ID))).thenReturn(1);

        BidSubmissionResult result = service.submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.resubmit()).isTrue();
        assertThat(result.rowCount()).isEqualTo(3);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbc).update(sql.capture(), eq(USERNAME), eq(USER_ID), eq(BID_ROUND_ID), eq(BUYER_CODE_ID));
        // The slide pattern is encoded in the SET ordering: last_valid_* reads
        // the *current* (pre-update) value of submitted_*, then submitted_*
        // is overwritten with bid_*. Postgres evaluates the right-hand side
        // of every SET clause against the row's pre-UPDATE state, so this is
        // safe even though both columns appear on both sides.
        assertThat(sql.getValue())
                .contains("last_valid_bid_quantity = submitted_bid_quantity")
                .contains("last_valid_bid_amount   = submitted_bid_amount")
                .contains("submitted_bid_quantity  = bid_quantity")
                .contains("submitted_bid_amount    = bid_amount");
    }

    @Test
    void submit_closedRound_throws() {
        // ownership passes
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
        BidRound round = bidRound(false, "Closed");
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(round));

        assertThatThrownBy(() ->
                service.submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("ROUND_CLOSED");

        verify(jdbc, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void submit_throwsWhenBuyerCodeNotOwnedByUser() {
        // ownership query (2-hop JOIN) returns 0 → user does not own buyer code.
        // Critically, this must throw BEFORE any DB read or write — otherwise
        // SUBMIT_BID_ROUND_SQL would flip the round-level submitted flag even
        // though the per-buyer-code SUBMIT_BID_DATA_SQL writes 0 rows.
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(0L);

        assertThatThrownBy(() ->
                service.submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("NOT_YOUR_BID_DATA");

        // No round lookup, no UPDATEs.
        verify(bidRoundRepo, never()).findById(any());
        verify(jdbc, never()).update(anyString(), any(Object[].class));
    }

    @Test
    void submit_administratorBypass_skipsOwnershipCheck() {
        // Mendix-parity admin bypass: an Administrator caller can act on
        // behalf of any bidder for diagnostic / recovery without owning the
        // buyer code. The ownership SQL must never run; the round + UPDATEs
        // proceed as normal.
        authenticateAsAdministrator();
        BidRound round = bidRound(false, "Started");
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(round));
        when(jdbc.update(anyString(), eq(USERNAME), eq(USER_ID), eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(3);
        when(jdbc.update(anyString(), eq(USER_ID), eq(BID_ROUND_ID))).thenReturn(1);

        BidSubmissionResult result = service.submit(USER_ID, USERNAME, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.rowCount()).isEqualTo(3);
        // Critical: ownership query must not have been issued.
        verify(jdbc, never()).queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID));
    }

    private static void authenticateAsAdministrator() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin@test.com", "n/a", List.of(new SimpleGrantedAuthority("ROLE_Administrator")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static BidData bidDataRow(int maximumQuantity) {
        BidData row = new BidData();
        row.setId(BID_DATA_ID);
        row.setBidRoundId(BID_ROUND_ID);
        row.setBuyerCodeId(BUYER_CODE_ID);
        row.setEcoid("ECO-1");
        row.setMergedGrade("ABC");
        row.setBuyerCodeType("Wholesale");
        row.setMaximumQuantity(maximumQuantity);
        return row;
    }

    /**
     * Build a BidRound stub. We can't drive {@code getRoundStatus()} directly
     * (it delegates to a lazy SchedulingAuction), so we use a Mockito spy that
     * overrides the delegating getter. Same for {@code getSubmitted()}, which
     * is a primitive so a simple setter works.
     */
    private static BidRound bidRound(boolean submitted, String status) {
        BidRound round = org.mockito.Mockito.spy(new BidRound());
        round.setSubmitted(submitted);
        org.mockito.Mockito.doReturn(status).when(round).getRoundStatus();
        return round;
    }

}
