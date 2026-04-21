package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionStatusPushPayloadTest {

    @Test
    @DisplayName("payload captures all canonical fields for the Snowflake push")
    void payload_capturesAllCanonicalFields() {
        Instant ts = Instant.parse("2026-04-21T16:00:00Z");

        AuctionStatusPushPayload p = new AuctionStatusPushPayload(
                42L, "Auction 2026 / Wk17", 100L, "2026 / Wk17",
                1, AuctionStatusAction.STARTED, ts, "ashirwadmittal");

        assertThat(p.auctionId()).isEqualTo(42L);
        assertThat(p.auctionTitle()).isEqualTo("Auction 2026 / Wk17");
        assertThat(p.weekId()).isEqualTo(100L);
        assertThat(p.weekDisplay()).isEqualTo("2026 / Wk17");
        assertThat(p.round()).isEqualTo(1);
        assertThat(p.action()).isEqualTo(AuctionStatusAction.STARTED);
        assertThat(p.transitionedAt()).isEqualTo(ts);
        assertThat(p.actor()).isEqualTo("ashirwadmittal");
    }

    @Test
    @DisplayName("action enum exposes STARTED and CLOSED, in that order")
    void action_supportsStartedAndClosed() {
        assertThat(AuctionStatusAction.values()).containsExactly(
                AuctionStatusAction.STARTED, AuctionStatusAction.CLOSED);
    }
}
