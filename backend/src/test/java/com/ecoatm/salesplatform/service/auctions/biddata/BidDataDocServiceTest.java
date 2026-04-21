package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.BidDataDocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidDataDocServiceTest {

    @Mock
    private BidDataDocRepository repo;

    private BidDataDocService service;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-04-23T10:00:00Z"), ZoneOffset.UTC);
        service = new BidDataDocService(repo, fixedClock);
    }

    @Test
    void getOrCreate_returnsExisting_whenPresent() {
        BidDataDoc existing = new BidDataDoc();
        existing.setUserId(1L);
        existing.setBuyerCodeId(2L);
        existing.setWeekId(3L);
        when(repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L))
            .thenReturn(Optional.of(existing));

        BidDataDoc result = service.getOrCreate(1L, 2L, 3L);

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    void getOrCreate_createsNewDoc_whenAbsent() {
        when(repo.findByUserIdAndBuyerCodeIdAndWeekId(1L, 2L, 3L))
            .thenReturn(Optional.empty());
        when(repo.save(any(BidDataDoc.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        BidDataDoc result = service.getOrCreate(1L, 2L, 3L);

        ArgumentCaptor<BidDataDoc> captor = ArgumentCaptor.forClass(BidDataDoc.class);
        verify(repo).save(captor.capture());
        BidDataDoc saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getBuyerCodeId()).isEqualTo(2L);
        assertThat(saved.getWeekId()).isEqualTo(3L);
        assertThat(saved.getCreatedDate()).isEqualTo(Instant.parse("2026-04-23T10:00:00Z"));
        assertThat(saved.getChangedDate()).isEqualTo(Instant.parse("2026-04-23T10:00:00Z"));
    }
}
