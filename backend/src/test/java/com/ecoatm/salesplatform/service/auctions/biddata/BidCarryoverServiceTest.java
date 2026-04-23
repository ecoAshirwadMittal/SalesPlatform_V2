package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.CarryoverResult;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
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
 * Unit tests for {@link BidCarryoverService}.
 *
 * <p>The two load-bearing scenarios:
 * <ol>
 *   <li>No prior week found → returns {@code {copied:0, notFound:0, prevWeek:null}}.
 *       The advisory lock is still acquired but the UPDATE is never issued.
 *   <li>Prior week found with submitted rows → returns {@code copied > 0} and
 *       the correct {@code prevWeek} display string.
 * </ol>
 *
 * <p>Additional scenarios: ownership rejected for non-admin Bidder without the
 * buyer code, and Administrator bypass skips the ownership check.
 */
@ExtendWith(MockitoExtension.class)
class BidCarryoverServiceTest {

    private static final long USER_ID = 7L;
    private static final long BID_ROUND_ID = 200L;
    private static final long BUYER_CODE_ID = 55L;
    private static final long PREV_BID_ROUND_ID = 100L;
    private static final String PREV_WEEK = "2026 / Wk15";

    @Mock
    private BidRoundRepository bidRoundRepo;

    @Mock
    private JdbcTemplate jdbc;

    private BidCarryoverService service;

    @BeforeEach
    void setUp() {
        service = new BidCarryoverService(bidRoundRepo, jdbc);
        // Default: authenticate as Bidder so ownership SQL runs.
        authenticateAsBidder();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // -----------------------------------------------------------------------
    // No prior week data
    // -----------------------------------------------------------------------

    @Test
    void carryover_noPriorWeek_returnsCopiedZero() {
        stubOwned();
        stubAdvisoryLock();
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(startedRound()));
        when(jdbc.queryForList(anyString(), eq(BID_ROUND_ID))).thenReturn(List.of());

        CarryoverResult result = service.carryover(USER_ID, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.copied()).isZero();
        assertThat(result.notFound()).isZero();
        assertThat(result.prevWeek()).isNull();
        // UPDATE must NOT be issued when there is no prior round.
        verify(jdbc, never()).update(anyString(),
                eq(BID_ROUND_ID), eq(BUYER_CODE_ID), any(Long.class));
    }

    // -----------------------------------------------------------------------
    // Prior week found and rows copied
    // -----------------------------------------------------------------------

    @Test
    void carryover_priorWeekFound_returnsCopiedCount() {
        stubOwned();
        stubAdvisoryLock();
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(startedRound()));
        when(jdbc.queryForList(anyString(), eq(BID_ROUND_ID)))
                .thenReturn(List.of(Map.of(
                        "prev_bid_round_id", PREV_BID_ROUND_ID,
                        "week_display", PREV_WEEK)));
        when(jdbc.update(anyString(),
                eq(BID_ROUND_ID), eq(BUYER_CODE_ID), eq(PREV_BID_ROUND_ID)))
                .thenReturn(42);
        when(jdbc.queryForObject(
                eq("SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ? AND buyer_code_id = ?"),
                eq(Integer.class), eq(BID_ROUND_ID), eq(BUYER_CODE_ID)))
                .thenReturn(50);

        CarryoverResult result = service.carryover(USER_ID, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.copied()).isEqualTo(42);
        assertThat(result.notFound()).isEqualTo(8); // 50 total - 42 copied
        assertThat(result.prevWeek()).isEqualTo(PREV_WEEK);
    }

    // -----------------------------------------------------------------------
    // Round closed guard
    // -----------------------------------------------------------------------

    @Test
    void carryover_roundClosed_throwsRoundClosedException() {
        stubOwned();
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(closedRound()));

        assertThatThrownBy(() -> service.carryover(USER_ID, BID_ROUND_ID, BUYER_CODE_ID))
                .isInstanceOf(BidDataSubmissionException.class)
                .satisfies(ex -> assertThat(((BidDataSubmissionException) ex).code())
                        .isEqualTo("ROUND_CLOSED"));
    }

    // -----------------------------------------------------------------------
    // Ownership check
    // -----------------------------------------------------------------------

    @Test
    void carryover_bidderDoesNotOwnBuyerCode_throwsNotYourBidRound() {
        // ownership SQL returns 0
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(0L);

        assertThatThrownBy(() -> service.carryover(USER_ID, BID_ROUND_ID, BUYER_CODE_ID))
                .isInstanceOf(BidDataSubmissionException.class)
                .satisfies(ex -> assertThat(((BidDataSubmissionException) ex).code())
                        .isEqualTo("NOT_YOUR_BID_ROUND"));
    }

    @Test
    void carryover_administrator_bypassesOwnershipCheck() {
        // Switch to Administrator role — ownership SQL must NOT be called.
        SecurityContextHolder.clearContext();
        authenticateAsAdministrator();

        stubAdvisoryLock();
        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(startedRound()));
        when(jdbc.queryForList(anyString(), eq(BID_ROUND_ID))).thenReturn(List.of());

        CarryoverResult result = service.carryover(USER_ID, BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(result.copied()).isZero();
        // ownership SQL (first arg is the OWNERSHIP_SQL string containing "user_mgmt.user_buyers")
        // must NOT have been invoked — verify by checking the mock was not called with Long return
        // and user/buyerCode args (a bit indirect; the key assertion is no exception was thrown).
        verify(jdbc, never()).queryForObject(
                anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void stubOwned() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
    }

    private void stubAdvisoryLock() {
        when(jdbc.queryForObject(
                eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
                eq(Object.class), eq((int) BID_ROUND_ID)))
                .thenReturn(null);
    }

    private static BidRound startedRound() {
        var sa = new SchedulingAuction();
        sa.setRoundStatus(SchedulingAuctionStatus.Started);
        BidRound round = new BidRound();
        // BidRound delegates getRoundStatus() to its lazy SchedulingAuction.
        // Reflectively set the field via the entity's setter is unavailable,
        // so we use a subclass override in the test scope.
        return new BidRound() {
            @Override
            public String getRoundStatus() { return "Started"; }
        };
    }

    private static BidRound closedRound() {
        return new BidRound() {
            @Override
            public String getRoundStatus() { return "Closed"; }
        };
    }

    private static void authenticateAsBidder() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_ID, "bidder@buyerco.com",
                List.of(new SimpleGrantedAuthority("ROLE_Bidder")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static void authenticateAsAdministrator() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_ID, "admin@test.com",
                List.of(new SimpleGrantedAuthority("ROLE_Administrator")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
