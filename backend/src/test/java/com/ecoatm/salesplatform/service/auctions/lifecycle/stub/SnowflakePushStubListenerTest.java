package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SnowflakePushStubListenerTest {

    private SnowflakePushStubListener listener;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        listener = new SnowflakePushStubListener();
        Logger l = (Logger) LoggerFactory.getLogger(SnowflakePushStubListener.class);
        appender = new ListAppender<>();
        appender.start();
        l.addAppender(appender);
        l.setLevel(Level.INFO);
    }

    @Test
    void onRoundStarted_anyRound_logsStub() {
        listener.onRoundStarted(new RoundStartedEvent(11L, 1, 100L, 999L));
        listener.onRoundStarted(new RoundStartedEvent(22L, 2, 100L, 999L));
        listener.onRoundStarted(new RoundStartedEvent(33L, 3, 100L, 999L));
        assertThat(appender.list).hasSize(3);
        assertThat(appender.list).allMatch(e ->
                e.getFormattedMessage().contains("[stub] Snowflake push (started)"));
    }

    @Test
    void onRoundClosed_anyRound_logsStub() {
        listener.onRoundClosed(new RoundClosedEvent(11L, 1, 100L, 999L));
        listener.onRoundClosed(new RoundClosedEvent(22L, 2, 100L, 999L));
        listener.onRoundClosed(new RoundClosedEvent(33L, 3, 100L, 999L));
        assertThat(appender.list).hasSize(3);
        assertThat(appender.list).allMatch(e ->
                e.getFormattedMessage().contains("[stub] Snowflake push (closed)"));
    }
}
