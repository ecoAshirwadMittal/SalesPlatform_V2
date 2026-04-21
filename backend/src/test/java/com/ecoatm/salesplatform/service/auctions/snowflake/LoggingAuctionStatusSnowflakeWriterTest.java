package com.ecoatm.salesplatform.service.auctions.snowflake;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingAuctionStatusSnowflakeWriterTest {

    private ListAppender<ILoggingEvent> appender;
    private Logger logger;

    @BeforeEach
    void attach() {
        logger = (Logger) LoggerFactory.getLogger(LoggingAuctionStatusSnowflakeWriter.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detach() {
        logger.detachAppender(appender);
    }

    @Test
    @DisplayName("push emits a single INFO line carrying every payload field and the deferred-writer marker")
    void push_emitsStructuredLineWithDeferredMarker() {
        AuctionStatusPushPayload p = new AuctionStatusPushPayload(
                42L, "Auction 2026 / Wk17", 100L, "2026 / Wk17", 1,
                AuctionStatusAction.STARTED, Instant.parse("2026-04-21T16:00:00Z"),
                "ashirwadmittal");

        new LoggingAuctionStatusSnowflakeWriter().push(p);

        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel().toString()).isEqualTo("INFO");
        String msg = event.getFormattedMessage();
        assertThat(msg)
                .contains("[deferred-writer]")
                .contains("action=STARTED")
                .contains("auctionId=42")
                .contains("auctionTitle=\"Auction 2026 / Wk17\"")
                .contains("weekId=100")
                .contains("weekDisplay=\"2026 / Wk17\"")
                .contains("round=1")
                .contains("transitionedAt=2026-04-21T16:00:00Z")
                .contains("actor=ashirwadmittal");
    }

    @Test
    @DisplayName("push renders CLOSED action correctly")
    void push_renderClosedAction() {
        AuctionStatusPushPayload p = new AuctionStatusPushPayload(
                7L, "Auction 2026 / Wk18", 101L, "2026 / Wk18", 3,
                AuctionStatusAction.CLOSED, Instant.parse("2026-04-28T12:00:00Z"),
                "system");

        new LoggingAuctionStatusSnowflakeWriter().push(p);

        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage())
                .contains("action=CLOSED")
                .contains("round=3")
                .contains("actor=system");
    }
}
