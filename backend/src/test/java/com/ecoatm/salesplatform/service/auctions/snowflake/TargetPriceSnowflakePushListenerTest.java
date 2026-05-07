package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
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
class TargetPriceSnowflakePushListenerTest {

    @Mock TargetPriceSnowflakeWriter writer;
    @Mock Environment env;
    @Mock SyncLogWriter syncLogWriter;

    @Test
    void short_circuits_when_snowflake_disabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(false);
        var listener = new TargetPriceSnowflakePushListener(writer, env, syncLogWriter);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer, never()).pushTargetPrices(anyLong(), anyInt());
    }

    @Test
    void pushes_target_round_when_enabled() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        var listener = new TargetPriceSnowflakePushListener(writer, env, syncLogWriter);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));

        verify(writer).pushTargetPrices(9001L, 2);
    }

    @Test
    void swallows_writer_exception() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("network err"))
            .when(writer).pushTargetPrices(anyLong(), anyInt());
        var listener = new TargetPriceSnowflakePushListener(writer, env, syncLogWriter);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(9001L, 1, 9001L, 9001L));
    }

    @Test
    void onTargetPriceRecalculated_writerThrows_writesFailedSyncLogRow() {
        when(env.getProperty("snowflake.enabled", Boolean.class, false)).thenReturn(true);
        doThrow(new RuntimeException("boom")).when(writer).pushTargetPrices(anyLong(), anyInt());
        var listener = new TargetPriceSnowflakePushListener(writer, env, syncLogWriter);

        listener.onTargetPriceRecalculated(new TargetPriceRecalculatedEvent(601L, 1, 601L, 601L));

        verify(syncLogWriter).writeFailed(
                eq("TARGET_PRICE"),
                eq("weekId=601,targetRound=2"),
                contains("boom"));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
