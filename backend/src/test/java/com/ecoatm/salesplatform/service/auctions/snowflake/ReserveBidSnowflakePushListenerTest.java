package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveBidSnowflakePushListenerTest {

    @Mock ReserveBidRepository repo;
    @Mock ReserveBidSnowflakeWriter writer;
    @Mock Environment env;

    ReserveBidSnowflakePushListener listener;

    @BeforeEach
    void setUp() {
        when(env.getProperty("eb.sync.enabled", Boolean.class, true)).thenReturn(true);
        listener = new ReserveBidSnowflakePushListener(repo, writer, env);
    }

    @Test
    void upsertEvent_invokesWriter() {
        ReserveBid rb = new ReserveBid();
        rb.setId(1L); rb.setProductId("1"); rb.setGrade("A_YYY");
        rb.setBid(new BigDecimal("10"));
        when(repo.findAllById(List.of(1L))).thenReturn(List.of(rb));

        listener.onChanged(new ReserveBidChangedEvent(List.of(1L), ReserveBidChangedEvent.Action.UPSERT));

        ArgumentCaptor<ReserveBidSnowflakePayload> cap = ArgumentCaptor.forClass(ReserveBidSnowflakePayload.class);
        verify(writer).upsert(cap.capture());
        assertThat(cap.getValue().rows()).hasSize(1);
        assertThat(cap.getValue().action()).isEqualTo("UPSERT");
    }

    @Test
    void featureFlagOff_noWriterCall() {
        when(env.getProperty("eb.sync.enabled", Boolean.class, true)).thenReturn(false);
        ReserveBidSnowflakePushListener off = new ReserveBidSnowflakePushListener(repo, writer, env);

        off.onChanged(new ReserveBidChangedEvent(List.of(1L), ReserveBidChangedEvent.Action.UPSERT));

        verifyNoInteractions(writer);
    }

    @Test
    void deleteEvent_sendsEmptyRowsWithDeleteAction() {
        listener.onChanged(new ReserveBidChangedEvent(List.of(5L), ReserveBidChangedEvent.Action.DELETE));
        ArgumentCaptor<ReserveBidSnowflakePayload> cap = ArgumentCaptor.forClass(ReserveBidSnowflakePayload.class);
        verify(writer).delete(cap.capture());
        assertThat(cap.getValue().action()).isEqualTo("DELETE");
    }
}
