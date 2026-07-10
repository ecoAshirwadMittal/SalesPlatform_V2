package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecoatm.salesplatform.service.partialcredit.CreditRequestFileDropParser.Outcome;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link CreditRequestFileDropParser}. Builds real
 * xlsx / docx bytes via Apache POI so the parser runs against
 * actual OOXML packages rather than mocked input streams.
 */
class CreditRequestFileDropParserTest {

    private CreditRequestFileDropParser parser;

    @BeforeEach
    void setUp() {
        parser = new CreditRequestFileDropParser();
    }

    // ── csv ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("csv — single-column file yields the kept barcodes in order")
    void csv_singleColumn() {
        MultipartFile file = csvFile(
                "barcodes.csv",
                "12345678",      // 8-digit pure number → kept
                "BC-XYZ-1",      // mixed alnum → kept
                "BC-XYZ-2",      // mixed alnum → kept
                "");             // blank line → ignored

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).containsExactly("12345678", "BC-XYZ-1", "BC-XYZ-2");
        assertThat(out.warnings()).isEmpty();
    }

    @Test
    @DisplayName("csv — comma-delimited rows use the FIRST column only")
    void csv_multiColumn_firstColumnOnly() {
        MultipartFile file = csvFile(
                "barcodes.csv",
                "BC-1,extra,more",
                "BC-2,more,stuff");

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).containsExactly("BC-1", "BC-2");
    }

    @Test
    @DisplayName("csv — short pure-digit runs (< 8 chars) are dropped + warned")
    void csv_dropsShortDigitRuns() {
        MultipartFile file = csvFile("noise.csv", "12", "1234567");

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).isEmpty();
        assertThat(out.warnings()).anyMatch(w -> w.contains("Skipped 2"));
    }

    @Test
    @DisplayName("csv — duplicates removed and surfaced as a warning")
    void csv_deduplicates() {
        MultipartFile file = csvFile("dupes.csv", "BC-1", "BC-1", "BC-2");

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).containsExactly("BC-1", "BC-2");
        assertThat(out.warnings()).anyMatch(w -> w.contains("Removed 1 duplicate"));
    }

    @Test
    @DisplayName("csv — surrounding quotes are stripped (Excel-export pattern)")
    void csv_stripsQuotedCells() {
        MultipartFile file = csvFile("quoted.csv", "\"BC-Q-1\"", "\"BC-Q-2\"");

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).containsExactly("BC-Q-1", "BC-Q-2");
    }

    // ── xlsx ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("xlsx — reads every cell on the first sheet, in row order")
    void xlsx_firstSheetOnly() throws Exception {
        byte[] bytes = xlsxBytes(new String[][] {
                {"BC-A-1", "ignored-second-col-actually-also-kept-if-valid"},
                {"BC-A-2", "BC-A-3"},
        });
        MultipartFile file = new MockMultipartFile(
                "file", "book.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);

        Outcome out = parser.parse(file);

        // Cells are kept verbatim; subset of mixed-alnum survives keep-rules.
        assertThat(out.barcodes()).contains("BC-A-1", "BC-A-2", "BC-A-3");
    }

    @Test
    @DisplayName("xlsx — empty workbook yields empty barcodes + warning")
    void xlsx_emptyWorkbook() throws Exception {
        byte[] bytes = xlsxBytes(new String[][]{});
        MultipartFile file = new MockMultipartFile(
                "file", "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bytes);

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).isEmpty();
    }

    // ── docx ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("docx — splits on whitespace within each paragraph")
    void docx_paragraphWhitespaceSplits() throws Exception {
        byte[] bytes = docxBytes("BC-D-1 BC-D-2 BC-D-3", "BC-D-4");
        MultipartFile file = new MockMultipartFile(
                "file", "doc.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                bytes);

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).containsExactly("BC-D-1", "BC-D-2", "BC-D-3", "BC-D-4");
    }

    // ── unsupported / empty ───────────────────────────────────────────

    @Test
    @DisplayName("empty file — returns empty result with an Empty-file warning")
    void empty_returnsWarning() {
        MultipartFile file = new MockMultipartFile("file", "x.csv", "text/csv", new byte[0]);

        Outcome out = parser.parse(file);

        assertThat(out.barcodes()).isEmpty();
        assertThat(out.warnings()).anyMatch(w -> w.toLowerCase().contains("empty"));
    }

    @Test
    @DisplayName("unsupported MIME — throws (maps to 415 at the controller)")
    void unsupported_throws() {
        MultipartFile file = new MockMultipartFile(
                "file", "scan.pdf", "application/pdf", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> parser.parse(file))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("pdf");
    }

    // ── row / entry cap (H-9) ─────────────────────────────────────────

    @Test
    @DisplayName("csv — a file above the row cap is rejected (413 at the controller)")
    void csv_aboveRowCap_throwsTooManyRows() {
        // 10_001 valid-looking barcode lines pushes rowsSeen past the 10_000 cap.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10_001; i++) {
            sb.append("12345678\n");
        }
        MultipartFile file = new MockMultipartFile(
                "file", "flood.csv", "text/csv", sb.toString().getBytes());

        assertThatThrownBy(() -> parser.parse(file))
                .isInstanceOfSatisfying(TooManyRowsException.class, ex -> {
                    assertThat(ex.limit()).isEqualTo(10_000);
                    assertThat(ex.matched()).isGreaterThan(10_000L);
                });
    }

    // ── keep-rule unit ────────────────────────────────────────────────

    @Test
    @DisplayName("looksLikeBarcode — covers each keep-rule branch")
    void looksLikeBarcodeBranches() {
        assertThat(CreditRequestFileDropParser.looksLikeBarcode("12345678")).isTrue();
        assertThat(CreditRequestFileDropParser.looksLikeBarcode("1234567")).isFalse(); // < 8 digits
        assertThat(CreditRequestFileDropParser.looksLikeBarcode("BC-XYZ-1")).isTrue();
        assertThat(CreditRequestFileDropParser.looksLikeBarcode("Header")).isFalse(); // letters only
        assertThat(CreditRequestFileDropParser.looksLikeBarcode("")).isFalse();
        assertThat(CreditRequestFileDropParser.looksLikeBarcode(null)).isFalse();
    }

    // ── helpers ───────────────────────────────────────────────────────

    private static MultipartFile csvFile(String name, String... rows) {
        StringBuilder sb = new StringBuilder();
        for (String r : rows) sb.append(r).append('\n');
        return new MockMultipartFile("file", name, "text/csv", sb.toString().getBytes());
    }

    private static byte[] xlsxBytes(String[][] rows) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet();
            for (int r = 0; r < rows.length; r++) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < rows[r].length; c++) {
                    row.createCell(c).setCellValue(rows[r][c]);
                }
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private static byte[] docxBytes(String... paragraphs) throws Exception {
        try (XWPFDocument doc = new XWPFDocument();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (String text : paragraphs) {
                XWPFParagraph p = doc.createParagraph();
                p.createRun().setText(text);
            }
            doc.write(out);
            return out.toByteArray();
        }
    }
}
