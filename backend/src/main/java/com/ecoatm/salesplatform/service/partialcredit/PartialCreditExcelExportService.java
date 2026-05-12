package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminListFilter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the admin landing list + every line of every result-set row
 * into a two-sheet xlsx for download (Sprint 4 chunk 7).
 *
 * <p>Hard cap: {@value #ROW_CAP} requests per export. Hitting the cap
 * raises {@link TooManyRowsException} which the controller maps to
 * {@code 413 Payload Too Large} with a {@code {error, limit, matched}}
 * body so the admin UI can show a "narrow your filters" toast inline.
 *
 * <p>The Lines sheet uses bulk {@code findByCreditRequestIdIn} queries
 * (added in this chunk) to avoid N+1 patterns over the 5,000-row cap.
 */
@Service
public class PartialCreditExcelExportService {

    public static final int ROW_CAP = 5_000;

    private static final String[] REQUEST_HEADERS = {
            "Request #", "Request Date", "Buyer Code", "Company", "Order #",
            "Reasons", "Status", "Requested Total", "Approved Total"
    };

    private static final String[] LINE_HEADERS = {
            "Request #", "Reason", "Identifier", "Brand / Expected Brand",
            "Model / Expected Model", "Grade", "Qty", "Amount Paid",
            "Amount To Credit", "Review Decision", "Action Recommendation"
    };

    private final AdminCreditRequestService adminService;
    private final CreditRequestStatusRepository statusRepository;
    private final BuyerCodeRepository buyerCodeRepository;
    private final MissingDeviceLineRepository missingRepo;
    private final WrongDeviceLineRepository wrongRepo;
    private final EncumberedDeviceLineRepository encumberedRepo;

    public PartialCreditExcelExportService(
            AdminCreditRequestService adminService,
            CreditRequestStatusRepository statusRepository,
            BuyerCodeRepository buyerCodeRepository,
            MissingDeviceLineRepository missingRepo,
            WrongDeviceLineRepository wrongRepo,
            EncumberedDeviceLineRepository encumberedRepo) {
        this.adminService = adminService;
        this.statusRepository = statusRepository;
        this.buyerCodeRepository = buyerCodeRepository;
        this.missingRepo = missingRepo;
        this.wrongRepo = wrongRepo;
        this.encumberedRepo = encumberedRepo;
    }

    /**
     * Build the xlsx bytes for the given filter. Throws
     * {@link TooManyRowsException} if the filter would resolve more than
     * {@link #ROW_CAP} requests so the caller can convert the error to
     * an HTTP 413 without ever materialising the oversized workbook.
     */
    public byte[] export(AdminListFilter filter) {
        // PageRequest of (ROW_CAP + 1) so getTotalElements lets us
        // distinguish "exactly at cap" from "over cap" in one call.
        Page<CreditRequest> page = adminService.listForAdmin(
                filter, PageRequest.of(0, ROW_CAP + 1));
        long matched = page.getTotalElements();
        if (matched > ROW_CAP) {
            throw new TooManyRowsException(ROW_CAP, matched);
        }
        List<CreditRequest> requests = page.getContent();

        Map<Long, CreditRequestStatus> statusById = loadStatusesById();
        Map<Long, BuyerCode> buyerCodeById = loadBuyerCodesById(requests);
        List<Long> requestIds = requests.stream().map(CreditRequest::getId).toList();
        List<MissingDeviceLine> missing = requestIds.isEmpty()
                ? List.of()
                : missingRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(requestIds);
        List<WrongDeviceLine> wrong = requestIds.isEmpty()
                ? List.of()
                : wrongRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(requestIds);
        List<EncumberedDeviceLine> encumbered = requestIds.isEmpty()
                ? List.of()
                : encumberedRepo.findByCreditRequestIdInOrderByCreditRequestIdAscIdAsc(requestIds);
        Map<Long, String> requestNumberById = new HashMap<>();
        for (CreditRequest cr : requests) {
            requestNumberById.put(cr.getId(), cr.getRequestNumber());
        }

        try (Workbook wb = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = headerStyle(wb);

            writeRequestsSheet(wb, headerStyle, requests, statusById, buyerCodeById);
            writeLinesSheet(wb, headerStyle, requestNumberById, missing, wrong, encumbered);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write xlsx", ex);
        }
    }

    // ── Requests sheet ────────────────────────────────────────────────

    private void writeRequestsSheet(
            Workbook wb,
            CellStyle headerStyle,
            List<CreditRequest> requests,
            Map<Long, CreditRequestStatus> statusById,
            Map<Long, BuyerCode> buyerCodeById) {
        Sheet sheet = wb.createSheet("Requests");
        Row header = sheet.createRow(0);
        for (int i = 0; i < REQUEST_HEADERS.length; i++) {
            var cell = header.createCell(i);
            cell.setCellValue(REQUEST_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        int r = 1;
        for (CreditRequest cr : requests) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(nullSafe(cr.getRequestNumber()));
            row.createCell(1).setCellValue(cr.getRequestDate() == null
                    ? ""
                    : cr.getRequestDate().toString());
            BuyerCode buyerCode = buyerCodeById.get(cr.getBuyerCodeId());
            row.createCell(2).setCellValue(buyerCode == null ? "" : nullSafe(buyerCode.getCode()));
            row.createCell(3).setCellValue(nullSafe(cr.getPartyName()));
            row.createCell(4).setCellValue(nullSafe(cr.getOrderNumber()));
            row.createCell(5).setCellValue(formatReasons(cr));
            CreditRequestStatus status = statusById.get(cr.getStatusId());
            row.createCell(6).setCellValue(status == null ? "" : nullSafe(status.getExternalStatusText()));
            row.createCell(7).setCellValue(bdToDouble(cr.getRequestedTotal()));
            row.createCell(8).setCellValue(bdToDouble(cr.getApprovedTotal()));
        }
        for (int i = 0; i < REQUEST_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ── Lines sheet ───────────────────────────────────────────────────

    private void writeLinesSheet(
            Workbook wb,
            CellStyle headerStyle,
            Map<Long, String> requestNumberById,
            List<MissingDeviceLine> missing,
            List<WrongDeviceLine> wrong,
            List<EncumberedDeviceLine> encumbered) {
        Sheet sheet = wb.createSheet("Lines");
        Row header = sheet.createRow(0);
        for (int i = 0; i < LINE_HEADERS.length; i++) {
            var cell = header.createCell(i);
            cell.setCellValue(LINE_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
        int r = 1;
        for (MissingDeviceLine line : missing) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(requestNumberById.getOrDefault(line.getCreditRequestId(), ""));
            row.createCell(1).setCellValue("MISSING");
            row.createCell(2).setCellValue(nullSafe(line.getBarcodeSubmitted()));
            row.createCell(3).setCellValue(nullSafe(line.getBrand()));
            row.createCell(4).setCellValue(nullSafe(line.getModel()));
            row.createCell(5).setCellValue(nullSafe(line.getGrade()));
            row.createCell(6).setCellValue(1);
            row.createCell(7).setCellValue(bdToDouble(line.getAmountPaid()));
            row.createCell(8).setCellValue(bdToDouble(line.getAmountToCredit()));
            row.createCell(9).setCellValue(line.getReviewDecision() == null
                    ? "PENDING"
                    : line.getReviewDecision().name());
            row.createCell(10).setCellValue("");
        }
        for (WrongDeviceLine line : wrong) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(requestNumberById.getOrDefault(line.getCreditRequestId(), ""));
            row.createCell(1).setCellValue("WRONG");
            // Identifier column carries expected_ecoatm_code falling
            // back to expected_barcode — the wrong-device flow keys off
            // the ecoatm code when the buyer provides it.
            String identifier = line.getExpectedEcoatmCode() != null
                    ? line.getExpectedEcoatmCode()
                    : nullSafe(line.getExpectedBarcode());
            row.createCell(2).setCellValue(identifier);
            row.createCell(3).setCellValue(nullSafe(line.getExpectedBrand()));
            row.createCell(4).setCellValue(nullSafe(line.getExpectedModel()));
            row.createCell(5).setCellValue(nullSafe(line.getExpectedGrade()));
            row.createCell(6).setCellValue(1);
            row.createCell(7).setCellValue(bdToDouble(line.getExpectedAmountPaid()));
            row.createCell(8).setCellValue(bdToDouble(line.getAmountToCredit()));
            row.createCell(9).setCellValue(line.getReviewDecision() == null
                    ? "PENDING"
                    : line.getReviewDecision().name());
            row.createCell(10).setCellValue(line.getActionRecommendation() == null
                    ? ""
                    : line.getActionRecommendation().name());
        }
        for (EncumberedDeviceLine line : encumbered) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(requestNumberById.getOrDefault(line.getCreditRequestId(), ""));
            row.createCell(1).setCellValue("ENCUMBERED");
            row.createCell(2).setCellValue(nullSafe(line.getBarcodeSubmitted()));
            row.createCell(3).setCellValue(nullSafe(line.getBrand()));
            row.createCell(4).setCellValue(nullSafe(line.getModel()));
            row.createCell(5).setCellValue(nullSafe(line.getGrade()));
            row.createCell(6).setCellValue(1);
            row.createCell(7).setCellValue(bdToDouble(line.getAmountPaid()));
            row.createCell(8).setCellValue(bdToDouble(line.getAmountToCredit()));
            row.createCell(9).setCellValue(line.getReviewDecision() == null
                    ? "PENDING"
                    : line.getReviewDecision().name());
            row.createCell(10).setCellValue("");
        }
        for (int i = 0; i < LINE_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────

    private Map<Long, CreditRequestStatus> loadStatusesById() {
        Map<Long, CreditRequestStatus> map = new HashMap<>();
        for (CreditRequestStatus row : statusRepository.findAll()) {
            map.put(row.getId(), row);
        }
        return map;
    }

    private Map<Long, BuyerCode> loadBuyerCodesById(List<CreditRequest> requests) {
        List<Long> ids = new ArrayList<>();
        for (CreditRequest cr : requests) {
            if (cr.getBuyerCodeId() != null) ids.add(cr.getBuyerCodeId());
        }
        if (ids.isEmpty()) return Map.of();
        Map<Long, BuyerCode> map = new HashMap<>();
        for (BuyerCode bc : buyerCodeRepository.findAllById(ids)) {
            map.put(bc.getId(), bc);
        }
        return map;
    }

    private static CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private static String formatReasons(CreditRequest cr) {
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(cr.getHasMissingDevice())) sb.append("Missing");
        if (Boolean.TRUE.equals(cr.getHasWrongDevice())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Wrong");
        }
        if (Boolean.TRUE.equals(cr.getHasEncumberedDevice())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Encumbered");
        }
        return sb.toString();
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }

    private static double bdToDouble(BigDecimal b) { return b == null ? 0 : b.doubleValue(); }
}
