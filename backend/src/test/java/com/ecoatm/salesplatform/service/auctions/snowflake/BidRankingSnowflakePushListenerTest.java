package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.service.auctions.SyncLogWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidRankingSnowflakePushListenerTest {

    @Mock BidRankingSnowflakeWriter writer;
    @Mock Environment env;
    @Mock SyncLogWriter syncLogWriter;

    @Test
    void short_circuits_when_snowflake_disabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(false);
        var listener = new BidRankingSnowflakePushListener(writer, env, syncLogWriter);

        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer, never()).pushBidRankings(anyLong(), anyInt());
    }

    @Test
    void pushes_when_enabled_passing_target_round() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        var listener = new BidRankingSnowflakePushListener(writer, env, syncLogWriter);

        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer).pushBidRankings(9001L, 2);
    }

    @Test
    void swallows_writer_exception() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("snowflake unreachable"))
            .when(writer).pushBidRankings(anyLong(), anyInt());
        var listener = new BidRankingSnowflakePushListener(writer, env, syncLogWriter);

        // Must not throw
        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(9001L, 1, 9001L, 9001L));
    }

    @Test
    void onBidRankingUpdated_writerThrows_writesFailedSyncLogRow() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("boom")).when(writer).pushBidRankings(anyLong(), anyInt());
        var listener = new BidRankingSnowflakePushListener(writer, env, syncLogWriter);

        listener.onBidRankingUpdated(new BidRankingUpdatedEvent(601L, 1, 601L, 601L));

        verify(syncLogWriter).writeFailed(
                eq("BID_RANKING"),
                eq("weekId=601,targetRound=2"),
                contains("boom"));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
