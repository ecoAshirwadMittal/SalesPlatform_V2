package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.CreateAuctionRequest;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.exception.AuctionAlreadyExistsException;
import com.ecoatm.salesplatform.exception.DuplicateAuctionTitleException;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
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

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    private static final Long WEEK_ID = 100L;
    private static final String WEEK_DISPLAY = "2026 / Wk17";
    private static final Instant WEEK_START = Instant.parse("2026-04-20T07:00:00Z");

    @Mock private AuctionRepository auctionRepository;
    @Mock private WeekRepository weekRepository;

    private AuctionService service;

    @BeforeEach
    void setUp() {
        service = new AuctionService(auctionRepository, weekRepository);
    }

    @Test
    @DisplayName("happy path — writes auction row only (no rounds)")
    void createAuction_happyPath() {
        Week week = week(WEEK_ID, WEEK_DISPLAY, WEEK_START);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
        when(auctionRepository.existsByWeekId(WEEK_ID)).thenReturn(false);
        when(auctionRepository.existsByAuctionTitleIgnoreCase(anyString())).thenReturn(false);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(inv -> {
            Auction a = inv.getArgument(0);
            setField(a, "id", 42L);
            return a;
        });

        CreateAuctionResponse response = service.createAuction(new CreateAuctionRequest(WEEK_ID, null));

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.auctionTitle()).isEqualTo("Auction 2026 / Wk17");
        assertThat(response.auctionStatus()).isEqualTo(AuctionStatus.Unscheduled.name());
        assertThat(response.weekId()).isEqualTo(WEEK_ID);
        assertThat(response.weekDisplay()).isEqualTo(WEEK_DISPLAY);

        ArgumentCaptor<Auction> auctionCaptor = ArgumentCaptor.forClass(Auction.class);
        verify(auctionRepository).save(auctionCaptor.capture());
        Auction saved = auctionCaptor.getValue();
        assertThat(saved.getAuctionStatus()).isEqualTo(AuctionStatus.Unscheduled);
        assertThat(saved.getWeekId()).isEqualTo(WEEK_ID);
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getChangedDate()).isNotNull();
    }

    @Test
    @DisplayName("custom suffix joins with single space after weekDisplay")
    void createAuction_customSuffix() {
        Week week = week(WEEK_ID, WEEK_DISPLAY, WEEK_START);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
        when(auctionRepository.existsByWeekId(WEEK_ID)).thenReturn(false);
        when(auctionRepository.existsByAuctionTitleIgnoreCase(anyString())).thenReturn(false);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(inv -> {
            Auction a = inv.getArgument(0);
            setField(a, "id", 43L);
            return a;
        });

        CreateAuctionResponse response = service.createAuction(
                new CreateAuctionRequest(WEEK_ID, "Launch"));

        assertThat(response.auctionTitle()).isEqualTo("Auction 2026 / Wk17 Launch");
    }

    @Test
    @DisplayName("whitespace-only suffix is treated as empty")
    void createAuction_whitespaceSuffix() {
        Week week = week(WEEK_ID, WEEK_DISPLAY, WEEK_START);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
        when(auctionRepository.existsByWeekId(WEEK_ID)).thenReturn(false);
        when(auctionRepository.existsByAuctionTitleIgnoreCase(anyString())).thenReturn(false);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(inv -> {
            Auction a = inv.getArgument(0);
            setField(a, "id", 44L);
            return a;
        });

        CreateAuctionResponse response = service.createAuction(
                new CreateAuctionRequest(WEEK_ID, "   "));

        assertThat(response.auctionTitle()).isEqualTo("Auction 2026 / Wk17");
    }

    @Test
    @DisplayName("missing week → EntityNotFoundException")
    void createAuction_missingWeek_throws() {
        when(weekRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createAuction(new CreateAuctionRequest(999L, null)))
                .isInstanceOf(EntityNotFoundException.class);

        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("auction already exists for week → AuctionAlreadyExistsException")
    void createAuction_duplicateWeek_throws() {
        Week week = week(WEEK_ID, WEEK_DISPLAY, WEEK_START);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
        when(auctionRepository.existsByWeekId(WEEK_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.createAuction(new CreateAuctionRequest(WEEK_ID, null)))
                .isInstanceOf(AuctionAlreadyExistsException.class);

        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("duplicate title (case-insensitive) → DuplicateAuctionTitleException")
    void createAuction_duplicateTitle_throws() {
        Week week = week(WEEK_ID, WEEK_DISPLAY, WEEK_START);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
        when(auctionRepository.existsByWeekId(WEEK_ID)).thenReturn(false);
        when(auctionRepository.existsByAuctionTitleIgnoreCase("Auction 2026 / Wk17"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.createAuction(new CreateAuctionRequest(WEEK_ID, null)))
                .isInstanceOf(DuplicateAuctionTitleException.class);

        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("null request → IllegalArgumentException (400)")
    void createAuction_nullWeekId_throws() {
        assertThatThrownBy(() -> service.createAuction(new CreateAuctionRequest(null, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---- helpers ----

    private Week week(Long id, String display, Instant start) {
        Week w = new Week();
        setField(w, "id", id);
        setField(w, "weekDisplay", display);
        setField(w, "weekStartDateTime", start);
        setField(w, "weekEndDateTime", start.plus(Duration.ofDays(7)));
        return w;
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = findField(target.getClass(), name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
