package com.ecoatm.salesplatform.service.auctions.snowflake;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionStatusSnowflakePushListenerTest {

    private static final Instant NOW = Instant.parse("2026-04-21T16:00:00Z");

    @Mock
    AuctionRepository auctionRepo;

    @Mock
    WeekRepository weekRepo;

    @Mock
    AuctionStatusSnowflakeWriter writer;

    Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);
    AuctionStatusSnowflakePushListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuctionStatusSnowflakePushListener(auctionRepo, weekRepo, writer, fixedClock, true);
    }

    private Auction auction(long id, long weekId, String title) {
        Auction a = new Auction();
        setId(a, id);
        a.setWeekId(weekId);
        a.setAuctionTitle(title);
        return a;
    }

    private Week week(long id, String display) {
        Week w = new Week();
        setId(w, id);
        setField(w, "weekDisplay", display);
        return w;
    }

    private static void setId(Object entity, long id) {
        setField(entity, "id", id);
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    @DisplayName("flag on + started event: writer receives STARTED payload with aggregate data and clock timestamp")
    void onRoundStarted_flagOn_invokesWriterWithStartedPayload() {
        when(auctionRepo.findById(42L)).thenReturn(Optional.of(auction(42L, 100L, "Auction 2026 / Wk17")));
        when(weekRepo.findById(100L)).thenReturn(Optional.of(week(100L, "2026 / Wk17")));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 42L, 100L));

        ArgumentCaptor<AuctionStatusPushPayload> cap = ArgumentCaptor.forClass(AuctionStatusPushPayload.class);
        verify(writer).push(cap.capture());
        AuctionStatusPushPayload p = cap.getValue();
        assertThat(p.action()).isEqualTo(AuctionStatusAction.STARTED);
        assertThat(p.auctionId()).isEqualTo(42L);
        assertThat(p.auctionTitle()).isEqualTo("Auction 2026 / Wk17");
        assertThat(p.weekId()).isEqualTo(100L);
        assertThat(p.weekDisplay()).isEqualTo("2026 / Wk17");
        assertThat(p.round()).isEqualTo(1);
        assertThat(p.transitionedAt()).isEqualTo(NOW);
        assertThat(p.actor()).isEqualTo("system");
    }

    @Test
    @DisplayName("flag on + closed event: writer receives CLOSED payload")
    void onRoundClosed_flagOn_invokesWriterWithClosedPayload() {
        when(auctionRepo.findById(42L)).thenReturn(Optional.of(auction(42L, 100L, "Auction 2026 / Wk17")));
        when(weekRepo.findById(100L)).thenReturn(Optional.of(week(100L, "2026 / Wk17")));

        listener.onRoundClosed(new RoundClosedEvent(302L, 3, 42L, 100L));

        ArgumentCaptor<AuctionStatusPushPayload> cap = ArgumentCaptor.forClass(AuctionStatusPushPayload.class);
        verify(writer).push(cap.capture());
        AuctionStatusPushPayload p = cap.getValue();
        assertThat(p.action()).isEqualTo(AuctionStatusAction.CLOSED);
        assertThat(p.round()).isEqualTo(3);
    }

    @Test
    @DisplayName("flag off: writer is never called and repositories are not queried")
    void event_flagOff_writerNeverCalled() {
        AuctionStatusSnowflakePushListener disabled =
                new AuctionStatusSnowflakePushListener(auctionRepo, weekRepo, writer, fixedClock, false);

        disabled.onRoundStarted(new RoundStartedEvent(301L, 1, 42L, 100L));
        disabled.onRoundClosed(new RoundClosedEvent(302L, 3, 42L, 100L));

        verifyNoInteractions(writer, auctionRepo, weekRepo);
    }

    @Test
    @DisplayName("auction missing: warn log, no writer call")
    void event_auctionNotFound_warnsAndReturns() {
        ListAppender<ILoggingEvent> appender = attachAppender();

        when(auctionRepo.findById(42L)).thenReturn(Optional.empty());
        when(weekRepo.findById(100L)).thenReturn(Optional.of(week(100L, "2026 / Wk17")));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 42L, 100L));

        verify(writer, never()).push(org.mockito.ArgumentMatchers.any());
        assertThat(appender.list).anyMatch(e ->
                e.getLevel() == Level.WARN
                        && e.getFormattedMessage().contains("aggregate missing")
                        && e.getFormattedMessage().contains("auctionId=42"));
    }

    @Test
    @DisplayName("writer throws: error log emitted, exception swallowed (does not bubble up)")
    void event_writerThrows_isSwallowed() {
        ListAppender<ILoggingEvent> appender = attachAppender();

        when(auctionRepo.findById(42L)).thenReturn(Optional.of(auction(42L, 100L, "Auction 2026 / Wk17")));
        when(weekRepo.findById(100L)).thenReturn(Optional.of(week(100L, "2026 / Wk17")));
        doThrow(new RuntimeException("snowflake down")).when(writer).push(org.mockito.ArgumentMatchers.any());

        // Listener must not throw — caller (Spring TX event infra) relies on this.
        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 42L, 100L));

        assertThat(appender.list).anyMatch(e ->
                e.getLevel() == Level.ERROR
                        && e.getFormattedMessage().contains("writer failed")
                        && e.getFormattedMessage().contains("auctionId=42"));
    }

    private ListAppender<ILoggingEvent> attachAppender() {
        Logger l = (Logger) LoggerFactory.getLogger(AuctionStatusSnowflakePushListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        l.addAppender(appender);
        l.setLevel(Level.DEBUG);
        return appender;
    }
}
