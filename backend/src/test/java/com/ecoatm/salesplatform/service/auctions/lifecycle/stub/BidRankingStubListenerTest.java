package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class BidRankingStubListenerTest {

    private BidRankingStubListener listener;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        listener = new BidRankingStubListener();
        Logger l = (Logger) LoggerFactory.getLogger(BidRankingStubListener.class);
        appender = new ListAppender<>();
        appender.start();
        l.addAppender(appender);
        l.setLevel(Level.INFO);
    }

    @Test
    void onRoundClosed_round1_logsStub() {
        listener.onRoundClosed(new RoundClosedEvent(11L, 1, 100L, 999L));
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage())
                .contains("[stub] Bid ranking")
                .contains("auctionId=100").contains("weekId=999").contains("round=1");
    }

    @Test
    void onRoundClosed_round2_logsStub() {
        listener.onRoundClosed(new RoundClosedEvent(22L, 2, 100L, 999L));
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage())
                .contains("[stub] Bid ranking")
                .contains("round=2");
    }

    @Test
    void onRoundClosed_round3_isIgnored() {
        listener.onRoundClosed(new RoundClosedEvent(33L, 3, 100L, 999L));
        assertThat(appender.list).isEmpty();
    }
}
