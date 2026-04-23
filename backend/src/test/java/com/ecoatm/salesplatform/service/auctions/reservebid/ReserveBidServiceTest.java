package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.dto.ReserveBidRequest;
import com.ecoatm.salesplatform.dto.ReserveBidRow;
import com.ecoatm.salesplatform.dto.ReserveBidUploadResult;
import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidSyncRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.ecoatm.salesplatform.model.auctions.ReserveBidSync;
import com.ecoatm.salesplatform.model.auctions.ReserveBidAudit;
import com.ecoatm.salesplatform.dto.ReserveBidSyncStatus;
import com.ecoatm.salesplatform.service.auctions.snowflake.ReserveBidSnowflakeReader;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class ReserveBidServiceTest {

    @Mock ReserveBidRepository repo;
    @Mock ReserveBidAuditRepository auditRepo;
    @Mock ReserveBidSyncRepository syncRepo;
    @Mock ApplicationEventPublisher publisher;

    ReserveBidService service;

    @BeforeEach
    void setUp() {
        service = new ReserveBidService(repo, auditRepo, syncRepo, publisher, null, null, null);
    }

    @Test
    void create_persistsRowAndPublishesEvent() {
        when(repo.existsByProductIdAndGrade("77101", "A_YYY")).thenReturn(false);
        when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
            ReserveBid rb = inv.getArgument(0);
            rb.setId(42L);
            return rb;
        });

        ReserveBidRow created = service.create(99L,
                new ReserveBidRequest("77101", "A_YYY", "Apple", "iPhone",
                        new BigDecimal("10"), null, null, null));

        assertThat(created.id()).isEqualTo(42L);
        verify(auditRepo, never()).save(any());   // no audit on CREATE

        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.UPSERT);
        assertThat(evt.getValue().changedIds()).containsExactly(42L);
    }

    @Test
    void create_rejectsDuplicateProductGrade() {
        when(repo.existsByProductIdAndGrade("77102", "A_YYY")).thenReturn(true);

        assertThatThrownBy(() -> service.create(99L,
                new ReserveBidRequest("77102", "A_YYY", null, null, BigDecimal.ONE, null, null, null)))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "DUPLICATE_PRODUCT_GRADE");
    }

    @Test
    void update_writesAuditOnPriceChange() {
        ReserveBid existing = new ReserveBid();
        existing.setId(5L);
        existing.setProductId("77103");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 5L,
                new ReserveBidRequest("77103", "A_YYY", null, null,
                        new BigDecimal("12.00"), null, null, null));

        verify(auditRepo).save(argThat(a ->
                a.getOldPrice().compareTo(new BigDecimal("10.00")) == 0 &&
                a.getNewPrice().compareTo(new BigDecimal("12.00")) == 0));
    }

    @Test
    void update_skipsAuditWhenPriceUnchanged() {
        ReserveBid existing = new ReserveBid();
        existing.setId(6L);
        existing.setProductId("77104");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(6L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 6L,
                new ReserveBidRequest("77104", "A_YYY", "NewBrand", null,
                        new BigDecimal("10.00"), null, null, null));

        verify(auditRepo, never()).save(any());
    }

    @Test
    void delete_publishesDeleteEvent() {
        ReserveBid existing = new ReserveBid();
        existing.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        service.delete(7L);

        verify(repo).delete(existing);
        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.DELETE);
        assertThat(evt.getValue().changedIds()).containsExactly(7L);
    }

    @Test
    void delete_throwsOnMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "RESERVE_BID_NOT_FOUND");
    }

    @Test
    void upload_createsNewRowsAndAuditsPriceChanges() throws Exception {
        when(repo.findByProductIdAndGrade("10001", "A_YYY")).thenReturn(Optional.of(existing("10001", "A_YYY", "40")));
        when(repo.findByProductIdAndGrade("10002", "A_YYY")).thenReturn(Optional.empty());
        when(repo.findByProductIdAndGrade("10003", "B_NYY")).thenReturn(Optional.empty());
        when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
            ReserveBid r = inv.getArgument(0);
            if (r.getId() == null) r.setId(100L);
            return r;
        });

        ReserveBidService real = new ReserveBidService(repo, auditRepo, syncRepo, publisher,
                null, new ReserveBidExcelParser(), null);

        byte[] bytes = java.nio.file.Files.readAllBytes(
                java.nio.file.Path.of("src/test/resources/fixtures/reserve-bid-sample.xlsx"));
        ReserveBidUploadResult result = real.upload(99L,
                new MockMultipartFile("file", "sample.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes));

        assertThat(result.created()).isEqualTo(2);
        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.auditsGenerated()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void upload_reportsErrorsWithoutAborting() throws Exception {
        when(repo.findByProductIdAndGrade(anyString(), anyString())).thenReturn(Optional.empty());
        when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
            ReserveBid r = inv.getArgument(0);
            if (r.getId() == null) r.setId(200L);
            return r;
        });

        ReserveBidService real = new ReserveBidService(repo, auditRepo, syncRepo, publisher,
                null, new ReserveBidExcelParser(), null);

        byte[] bytes = java.nio.file.Files.readAllBytes(
                java.nio.file.Path.of("src/test/resources/fixtures/reserve-bid-with-errors.xlsx"));
        ReserveBidUploadResult result = real.upload(99L,
                new MockMultipartFile("file", "errors.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes));

        assertThat(result.created()).isEqualTo(1);
        assertThat(result.errors()).hasSize(3);
        assertThat(result.errors()).anyMatch(e -> "DUPLICATE_IN_SHEET".equals(e.reason()));
        assertThat(result.errors()).anyMatch(e -> "MISSING_GRADE".equals(e.reason()));
        assertThat(result.errors()).anyMatch(e -> "NEGATIVE_PRICE".equals(e.reason()));
    }

    @Test
    void download_streamsCurrentRowsAsXlsx() throws Exception {
        when(repo.findAll()).thenReturn(java.util.List.of(
                existing("11001", "A_YYY", "10"),
                existing("11002", "B_NYY", "20")));

        ReserveBidExcelWriter writer = new ReserveBidExcelWriter();

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writer.writeAll(repo.findAll(), out);

        try (org.apache.poi.ss.usermodel.Workbook wb =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(out.toByteArray()))) {
            org.apache.poi.ss.usermodel.Sheet s = wb.getSheetAt(0);
            assertThat(s.getPhysicalNumberOfRows()).isEqualTo(3);
            assertThat(s.getRow(1).getCell(0).getStringCellValue()).isEqualTo("11001");
        }
    }

    @Test
    void findAudit_returnsPagedTrail() {
        ReserveBid rb = existing("55001", "A_YYY", "10");
        when(repo.findById(55001L)).thenReturn(Optional.of(rb));

        ReserveBidAudit a = new ReserveBidAudit();
        a.setId(1L);
        a.setReserveBidId(55001L);
        a.setOldPrice(new BigDecimal("10"));
        a.setNewPrice(new BigDecimal("12"));
        a.setCreatedDate(Instant.now());
        when(auditRepo.findByReserveBidIdOrderByCreatedDateDesc(eq(55001L), any()))
                .thenReturn(new PageImpl<>(java.util.List.of(a), PageRequest.of(0, 20), 1));

        var resp = service.findAudit(55001L, 0, 20);
        assertThat(resp.rows()).hasSize(1);
        assertThat(resp.rows().get(0).newPrice()).isEqualByComparingTo("12");
    }

    @Test
    void syncStatus_reportsNeverSyncedWhenNullWatermark() {
        ReserveBidSync sync = new ReserveBidSync();
        sync.setLastSyncDatetime(null);
        when(syncRepo.findFirstByOrderByIdAsc()).thenReturn(Optional.of(sync));

        ReserveBidSnowflakeReader reader = mock(ReserveBidSnowflakeReader.class);
        when(reader.fetchMaxUploadTime()).thenReturn(Optional.of(Instant.parse("2026-04-01T00:00:00Z")));

        ReserveBidService svc = new ReserveBidService(repo, auditRepo, syncRepo, publisher, reader, null, null);
        var status = svc.syncStatus();
        assertThat(status.state()).isEqualTo(ReserveBidSyncStatus.NEVER_SYNCED);
    }

    private static ReserveBid existing(String pid, String grade, String bid) {
        ReserveBid r = new ReserveBid();
        r.setId(Long.parseLong(pid));
        r.setProductId(pid);
        r.setGrade(grade);
        r.setBid(new java.math.BigDecimal(bid));
        return r;
    }
}
