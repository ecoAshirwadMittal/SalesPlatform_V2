package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidImportResult;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BidImportService")
class BidImportServiceTest {

    private static final long USER_ID       = 42L;
    private static final long BID_ROUND_ID  = 9001L;
    private static final long BUYER_CODE_ID = 50L;
    private static final long BID_DATA_ID   = 555001L;

    @Mock
    private JdbcTemplate jdbc;

    private BidImportService service;

    @BeforeEach
    void setUp() {
        service = new BidImportService(jdbc);
    }

    // ---------------------------------------------------------------------------
    // Happy path
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("happy path: valid xlsx — round status Started, one row updated")
    void importBids_happyPath_updatesOneRow() throws Exception {
        // Arrange
        stubRoundOpen();
        when(jdbc.queryForList(anyString(), eq(Long.class), anyLong(), anyLong()))
                .thenReturn(List.of(BID_DATA_ID));
        when(jdbc.batchUpdate(anyString(), any(List.class)))
                .thenReturn(new int[]{1});

        MockMultipartFile file = buildXlsx(new Object[][]{
                {BID_DATA_ID, "45.00", "10"}   // id, price, qty cap
        });

        // Act
        BidImportResult result = service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file);

        // Assert
        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }

    @Test
    @DisplayName("happy path: blank Qty. Cap cell is treated as no-cap (null bid_quantity)")
    void importBids_blankQtyCap_noCapSentinel() throws Exception {
        stubRoundOpen();
        when(jdbc.queryForList(anyString(), eq(Long.class), anyLong(), anyLong()))
                .thenReturn(List.of(BID_DATA_ID));
        when(jdbc.batchUpdate(anyString(), any(List.class)))
                .thenReturn(new int[]{1});

        // Empty string in Qty. Cap column
        MockMultipartFile file = buildXlsx(new Object[][]{
                {BID_DATA_ID, "38.00", ""}
        });

        BidImportResult result = service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file);

        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }

    // ---------------------------------------------------------------------------
    // Validation failures
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("unknown row id returns error — no DB update performed")
    void importBids_unknownId_returnsRowError() throws Exception {
        stubRoundOpen();
        // valid ids for this slice: only 555001; the xlsx sends 999999
        when(jdbc.queryForList(anyString(), eq(Long.class), anyLong(), anyLong()))
                .thenReturn(List.of(BID_DATA_ID));

        long unknownId = 999_999L;
        MockMultipartFile file = buildXlsx(new Object[][]{
                {unknownId, "30.00", "5"}
        });

        BidImportResult result = service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file);

        assertThat(result.updated()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(2); // row 2 (data starts at 2)
        assertThat(result.errors().get(0).message()).contains("does not belong");

        // No batch update should have been issued
        verify(jdbc, never()).batchUpdate(anyString(), any(List.class));
    }

    @Test
    @DisplayName("negative price returns row error")
    void importBids_negativePrice_returnsRowError() throws Exception {
        stubRoundOpen();
        when(jdbc.queryForList(anyString(), eq(Long.class), anyLong(), anyLong()))
                .thenReturn(List.of(BID_DATA_ID));

        MockMultipartFile file = buildXlsx(new Object[][]{
                {BID_DATA_ID, "-5.00", "10"}
        });

        BidImportResult result = service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file);

        assertThat(result.updated()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("negative");
    }

    @Test
    @DisplayName("round closed: ROUND_CLOSED exception thrown before parsing")
    void importBids_roundClosed_throwsException() throws Exception {
        // Stub round status as Closed
        when(jdbc.queryForObject(anyString(), eq(String.class), anyLong()))
                .thenReturn("Closed");

        MockMultipartFile file = buildXlsx(new Object[][]{
                {BID_DATA_ID, "30.00", "5"}
        });

        assertThatThrownBy(() -> service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file))
                .isInstanceOf(BidDataSubmissionException.class)
                .satisfies(e -> assertThat(((BidDataSubmissionException) e).code())
                        .isEqualTo("ROUND_CLOSED"));
    }

    @Test
    @DisplayName("oversized file: IMPORT_INVALID exception thrown immediately")
    void importBids_oversizedFile_throwsException() throws Exception {
        // 6 MB in-memory fake — but we only need the size check
        // Build a file whose reported size exceeds 5 MB without allocating 6 MB.
        // MockMultipartFile.getSize() returns the byte array length, so we use a
        // small content but manually set the size via a subclass workaround.
        // Instead, build a real 1-byte file but trick getSize. Easiest: just build
        // a file with content exactly 5 * 1024 * 1024 + 1 bytes — that is too large
        // to allocate in a unit test. Use a custom approach: pass a file with
        // an overridden size. Since MockMultipartFile doesn't allow that, we
        // verify the guard triggers for a file size we can construct cheaply.
        // Strategy: use a real xlsx with a very large nominal size by relying on
        // the fact that the service checks file.getSize() before opening the stream.
        //
        // We wrap MockMultipartFile to override getSize() only.
        byte[] tiny = {0x50, 0x4B, 0x03, 0x04}; // xlsx magic bytes
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", tiny) {
            @Override
            public long getSize() {
                return 6L * 1024 * 1024; // pretend 6 MB
            }
        };

        assertThatThrownBy(() -> service.importBids(USER_ID, BID_ROUND_ID, BUYER_CODE_ID, file))
                .isInstanceOf(BidDataValidationException.class)
                .satisfies(e -> assertThat(((BidDataValidationException) e).code())
                        .isEqualTo("IMPORT_INVALID"));
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /** Stubs round status as "Started" (i.e. open). */
    private void stubRoundOpen() {
        when(jdbc.queryForObject(anyString(), eq(String.class), anyLong()))
                .thenReturn("Started");
    }

    /**
     * Builds a minimal xlsx where:
     * <ul>
     *   <li>Row 0 = header (same order as the export)</li>
     *   <li>Rows 1..n = data rows, each array is [id, price, qty_cap]</li>
     * </ul>
     * Only cols 9 (price), 10 (qty cap), and 11 (id) are written;
     * the other columns are left blank (the service ignores them).
     */
    private static MockMultipartFile buildXlsx(Object[][] dataRows) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            var sheet = wb.createSheet("Bids");

            // Header row
            var header = sheet.createRow(0);
            String[] headers = {
                    "Product Id", "Brand", "Model", "Model Name", "Grade",
                    "Carrier", "Added", "Avail. Qty", "Target Price", "Price",
                    "Qty. Cap", "Id"
            };
            for (int c = 0; c < headers.length; c++) {
                header.createCell(c).setCellValue(headers[c]);
            }

            // Data rows
            for (int r = 0; r < dataRows.length; r++) {
                var row = sheet.createRow(r + 1);
                Object[] cols = dataRows[r];
                // col 9 — price
                String priceStr = (String) cols[1];
                try {
                    row.createCell(9).setCellValue(Double.parseDouble(priceStr));
                } catch (NumberFormatException e) {
                    row.createCell(9).setCellValue(priceStr);
                }
                // col 10 — qty cap (may be blank)
                String qtyStr = (String) cols[2];
                if (qtyStr.isBlank()) {
                    row.createCell(10).setCellValue("");
                } else {
                    try {
                        row.createCell(10).setCellValue(Double.parseDouble(qtyStr));
                    } catch (NumberFormatException e) {
                        row.createCell(10).setCellValue(qtyStr);
                    }
                }
                // col 11 — id
                row.createCell(11).setCellValue((double) (Long) cols[0]);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return new MockMultipartFile("file", "bids.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    baos.toByteArray());
        }
    }
}
