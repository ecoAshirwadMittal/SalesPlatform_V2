package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class R3InitStubListenerTest {

    private R3InitStubListener listener;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        listener = new R3InitStubListener();
        Logger l = (Logger) LoggerFactory.getLogger(R3InitStubListener.class);
        appender = new ListAppender<>();
        appender.start();
        l.addAppender(appender);
        l.setLevel(Level.INFO);
    }

    @Test
    void onRoundStarted_round3_logsStub() {
        listener.onRoundStarted(new RoundStartedEvent(33L, 3, 100L, 999L));
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage())
                .contains("[stub] R3 init")
                .contains("auctionId=100").contains("weekId=999").contains("roundId=33");
    }

    @Test
    void onRoundStarted_round1_isIgnored() {
        listener.onRoundStarted(new RoundStartedEvent(11L, 1, 100L, 999L));
        assertThat(appender.list).isEmpty();
    }

    @Test
    void onRoundStarted_round2_isIgnored() {
        listener.onRoundStarted(new RoundStartedEvent(22L, 2, 100L, 999L));
        assertThat(appender.list).isEmpty();
    }
}
