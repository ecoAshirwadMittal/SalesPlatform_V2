package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ActionRecommendation;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminListFilter;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Unit tests for {@link PartialCreditExcelExportService}. The xlsx
 * payload is round-tripped through Apache POI in the assertions so we
 * verify the actual sheet shape end-to-end without needing an HTTP
 * layer.
 */
@ExtendWith(MockitoExtension.class)
class PartialCreditExcelExportServiceTest {

    @Mock private AdminCreditRequestService adminService;
    @Mock private CreditRequestStatusRepository statusRepository;
    @Mock private BuyerCodeRepository buyerCodeRepository;
    @Mock private MissingDeviceLineRepository missingRepo;
    @Mock private WrongDeviceLineRepository wrongRepo;
    @Mock private EncumberedDeviceLineRepository encumberedRepo;

    private PartialCreditExcelExportService service;

    @BeforeEach
    void setUp() {
        service = new PartialCreditExcelExportService(
                adminService, statusRepository, buyerCodeRepository,
                missingRepo, wrongRepo, encumberedRepo);
    }

    @Test
    @DisplayName("export — produces a 2-sheet workbook (Requests + Lines) with headers")
    void export_twoSheetStructure() throws Exception {
        when(adminService.listForAdmin(any(), any())).thenReturn(emptyPage());
        when(statusRepository.findAll()).thenReturn(List.of());

        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            assertThat(wb.getNumberOfSheets()).isEqualTo(2);
            assertThat(wb.getSheetAt(0).getSheetName()).isEqualTo("Requests");
            assertThat(wb.getSheetAt(1).getSheetName()).isEqualTo("Lines");
            // Header row exists on both sheets.
            assertThat(wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).isEqualTo("Request #");
            assertThat(wb.getSheetAt(1).getRow(0).getCell(0).getStringCellValue()).isEqualTo("Request #");
        }
    }

    @Test
    @DisplayName("export — empty filter result still writes only the header row")
    void export_emptyResultSet() throws Exception {
        when(adminService.listForAdmin(any(), any())).thenReturn(emptyPage());
        when(statusRepository.findAll()).thenReturn(List.of());

        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            // Headers only; no data rows.
            assertThat(wb.getSheetAt(0).getLastRowNum()).isEqualTo(0);
            assertThat(wb.getSheetAt(1).getLastRowNum()).isEqualTo(0);
        }
        // No request IDs → bulk-line repos must not be hit at all.
        verify(missingRepo, never()).findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList());
        verify(wrongRepo, never()).findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList());
        verify(encumberedRepo, never()).findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList());
    }

    @Test
    @DisplayName("export — Requests sheet body row includes request #, order #, status text, totals")
    void export_requestsBody() throws Exception {
        CreditRequest cr = request(100L, "PCR-1", "SO-1", 500L, 1L);
        cr.setRequestedTotal(new BigDecimal("125.50"));
        cr.setApprovedTotal(new BigDecimal("80.00"));
        cr.setPartyName("Acme");
        cr.setHasMissingDevice(true);
        when(adminService.listForAdmin(any(), any()))
                .thenReturn(new PageImpl<>(List.of(cr), PageRequest.of(0, 5001), 1));
        when(statusRepository.findAll()).thenReturn(List.of(statusRow(1L, SystemStatus.APPROVED, "Approved")));
        BuyerCode bc = new BuyerCode();
        bc.setId(500L);
        bc.setCode("20399");
        when(buyerCodeRepository.findAllById(anyList())).thenReturn(List.of(bc));
        when(missingRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());
        when(wrongRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());
        when(encumberedRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());

        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            var row = wb.getSheetAt(0).getRow(1);
            assertThat(row.getCell(0).getStringCellValue()).isEqualTo("PCR-1");
            assertThat(row.getCell(2).getStringCellValue()).isEqualTo("20399");
            assertThat(row.getCell(3).getStringCellValue()).isEqualTo("Acme");
            assertThat(row.getCell(4).getStringCellValue()).isEqualTo("SO-1");
            assertThat(row.getCell(5).getStringCellValue()).contains("Missing");
            assertThat(row.getCell(6).getStringCellValue()).isEqualTo("Approved");
            assertThat(row.getCell(7).getNumericCellValue()).isEqualTo(125.50);
            assertThat(row.getCell(8).getNumericCellValue()).isEqualTo(80.00);
        }
    }

    @Test
    @DisplayName("export — Lines sheet renders one row per missing / wrong / encumbered line in order")
    void export_linesSheetMixedReasons() throws Exception {
        CreditRequest cr = request(100L, "PCR-1", "SO-1", 500L, 1L);
        when(adminService.listForAdmin(any(), any()))
                .thenReturn(new PageImpl<>(List.of(cr), PageRequest.of(0, 5001), 1));
        when(statusRepository.findAll()).thenReturn(List.of(statusRow(1L, SystemStatus.UNDER_REVIEW, "Under Review")));
        when(buyerCodeRepository.findAllById(anyList())).thenReturn(List.of());

        MissingDeviceLine missing = new MissingDeviceLine();
        missing.setId(10L);
        missing.setCreditRequestId(100L);
        missing.setBarcodeSubmitted("BC-M-1");
        missing.setBrand("Apple");
        missing.setModel("iPhone 12");
        missing.setGrade("A");
        missing.setAmountPaid(new BigDecimal("50.00"));
        missing.setAmountToCredit(new BigDecimal("50.00"));
        missing.setReviewDecision(ReviewDecision.ACCEPTED);
        when(missingRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList()))
                .thenReturn(List.of(missing));

        WrongDeviceLine wrong = new WrongDeviceLine();
        wrong.setId(20L);
        wrong.setCreditRequestId(100L);
        wrong.setExpectedBarcode("BC-W-1");
        wrong.setExpectedEcoatmCode("ECO-123");
        wrong.setExpectedBrand("Apple");
        wrong.setExpectedModel("iPhone XR");
        wrong.setExpectedAmountPaid(new BigDecimal("70.00"));
        wrong.setAmountToCredit(new BigDecimal("20.00"));
        wrong.setReviewDecision(ReviewDecision.DECLINED);
        wrong.setActionRecommendation(ActionRecommendation.DECLINE);
        when(wrongRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList()))
                .thenReturn(List.of(wrong));

        EncumberedDeviceLine encumbered = new EncumberedDeviceLine();
        encumbered.setId(30L);
        encumbered.setCreditRequestId(100L);
        encumbered.setBarcodeSubmitted("BC-E-1");
        encumbered.setBrand("Samsung");
        encumbered.setModel("Galaxy S20");
        encumbered.setGrade("B");
        encumbered.setAmountPaid(new BigDecimal("90.00"));
        encumbered.setAmountToCredit(new BigDecimal("90.00"));
        when(encumberedRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList()))
                .thenReturn(List.of(encumbered));

        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            var lines = wb.getSheetAt(1);
            // Header + 3 body rows.
            assertThat(lines.getLastRowNum()).isEqualTo(3);
            assertThat(lines.getRow(1).getCell(1).getStringCellValue()).isEqualTo("MISSING");
            assertThat(lines.getRow(1).getCell(2).getStringCellValue()).isEqualTo("BC-M-1");
            assertThat(lines.getRow(1).getCell(9).getStringCellValue()).isEqualTo("ACCEPTED");
            // Wrong row uses expected_ecoatm_code as the identifier
            // column when present.
            assertThat(lines.getRow(2).getCell(1).getStringCellValue()).isEqualTo("WRONG");
            assertThat(lines.getRow(2).getCell(2).getStringCellValue()).isEqualTo("ECO-123");
            assertThat(lines.getRow(2).getCell(10).getStringCellValue()).isEqualTo("DECLINE");
            assertThat(lines.getRow(3).getCell(1).getStringCellValue()).isEqualTo("ENCUMBERED");
            assertThat(lines.getRow(3).getCell(2).getStringCellValue()).isEqualTo("BC-E-1");
        }
    }

    @Test
    @DisplayName("export — null review_decision renders as 'PENDING' (not blank)")
    void export_pendingDecisionRenderedExplicitly() throws Exception {
        CreditRequest cr = request(100L, "PCR-1", "SO-1", 500L, 1L);
        when(adminService.listForAdmin(any(), any()))
                .thenReturn(new PageImpl<>(List.of(cr), PageRequest.of(0, 5001), 1));
        when(statusRepository.findAll()).thenReturn(List.of(statusRow(1L, SystemStatus.PENDING_APPROVAL, "Pending Approval")));
        when(buyerCodeRepository.findAllById(anyList())).thenReturn(List.of());
        MissingDeviceLine line = new MissingDeviceLine();
        line.setId(10L);
        line.setCreditRequestId(100L);
        line.setBarcodeSubmitted("BC-1");
        line.setAmountPaid(new BigDecimal("10"));
        // Intentionally do NOT set reviewDecision — null in the entity.
        when(missingRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of(line));
        when(wrongRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());
        when(encumberedRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());

        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
            assertThat(wb.getSheetAt(1).getRow(1).getCell(9).getStringCellValue()).isEqualTo("PENDING");
        }
    }

    @Test
    @DisplayName("export — over-cap result throws TooManyRowsException carrying limit + matched")
    void export_overCap_throws() {
        List<CreditRequest> tooMany = new ArrayList<>();
        for (int i = 0; i < 5001; i++) {
            tooMany.add(request((long) i, "PCR-" + i, "SO-" + i, 500L, 1L));
        }
        when(adminService.listForAdmin(any(), any()))
                .thenReturn(new PageImpl<>(tooMany, PageRequest.of(0, 5001), 6_123));

        AdminListFilter filter = new AdminListFilter(null, null, null, null, null, null);
        assertThatThrownBy(() -> service.export(filter))
                .isInstanceOf(TooManyRowsException.class)
                .satisfies(ex -> {
                    TooManyRowsException tmre = (TooManyRowsException) ex;
                    assertThat(tmre.limit()).isEqualTo(5000);
                    assertThat(tmre.matched()).isEqualTo(6123L);
                });
    }

    @Test
    @DisplayName("export — exactly 5000 rows is allowed (cap is inclusive)")
    void export_atCap_allowed() {
        List<CreditRequest> exact = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            exact.add(request((long) i, "PCR-" + i, "SO-" + i, 500L, 1L));
        }
        when(adminService.listForAdmin(any(), any()))
                .thenReturn(new PageImpl<>(exact, PageRequest.of(0, 5001), 5000));
        when(statusRepository.findAll()).thenReturn(List.of(statusRow(1L, SystemStatus.APPROVED, "Approved")));
        when(buyerCodeRepository.findAllById(anyList())).thenReturn(List.of());
        when(missingRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());
        when(wrongRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());
        when(encumberedRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(anyList())).thenReturn(List.of());

        // Should not throw.
        byte[] xlsx = service.export(new AdminListFilter(null, null, null, null, null, null));
        assertThat(xlsx).isNotEmpty();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static Page<CreditRequest> emptyPage() {
        return new PageImpl<>(List.of(), PageRequest.of(0, 5001), 0);
    }

    private static CreditRequest request(Long id, String number, String order, Long buyerCodeId, Long statusId) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber(number);
        cr.setOrderNumber(order);
        cr.setBuyerCodeId(buyerCodeId);
        cr.setStatusId(statusId);
        cr.setRequestDate(Instant.parse("2026-05-12T00:00:00Z"));
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }

    private static CreditRequestStatus statusRow(Long id, SystemStatus s, String externalText) {
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(id);
        row.setSystemStatus(s);
        row.setExternalStatusText(externalText);
        return row;
    }
}
