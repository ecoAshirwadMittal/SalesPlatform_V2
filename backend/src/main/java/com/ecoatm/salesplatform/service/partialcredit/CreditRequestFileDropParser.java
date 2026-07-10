package com.ecoatm.salesplatform.service.partialcredit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a buyer-uploaded file (xlsx / csv / docx) into a deduped list
 * of plausible-barcode strings for the wizard Step 2 file-drop.
 *
 * <p>Keep-rules (per Sprint 4 plan §3.5):
 * <ul>
 *   <li>Strings containing letters mixed with digits are kept verbatim.</li>
 *   <li>Pure-digit runs of {@code >= 8} characters are kept.</li>
 *   <li>Everything else is dropped — and the caller gets a {@code warnings}
 *       count so the UI can surface "skipped N cells that didn't look like
 *       barcodes".</li>
 * </ul>
 *
 * <p>Files are detected by content-type first, filename extension second.
 * Unknown types throw {@link UnsupportedOperationException} so the
 * controller can map them to {@code 415 Unsupported Media Type}.
 *
 * <p>Note: returns an {@link Outcome} record carrying barcodes +
 * warnings; the caller decides how to merge them with whatever the
 * buyer already typed into the textarea.
 */
@Service
public class CreditRequestFileDropParser {

    /** Heuristic: any digit-only string with at least this many digits
     *  is treated as a candidate barcode. The wizard's barcode
     *  reconciliation will reject genuinely-invalid values anyway. */
    private static final int MIN_DIGIT_RUN_LENGTH = 8;

    /** Matches a token that has both letters and digits (an "alnum
     *  with mixed character classes" — most real barcodes do). */
    private static final Pattern MIXED_ALNUM = Pattern.compile(".*[A-Za-z].*\\d.*|.*\\d.*[A-Za-z].*");

    /**
     * Hard cap on rows scanned and tokens collected, mirroring
     * {@code BidImportService.MAX_DATA_ROWS} (security review 2026-07-10, H-9).
     * {@code /parse-barcodes} is reachable by any Bidder/SalesRep and was
     * previously unbounded — a crafted csv/xlsx/docx could force this
     * DOM-loading parser to buffer millions of tokens on the heap. The breach
     * throws {@link TooManyRowsException} (413 at the controller) rather than
     * silently truncating, so the buyer is told to shrink the file.
     */
    private static final int MAX_ENTRIES = 10_000;

    public record Outcome(List<String> barcodes, List<String> warnings) {}

    public Outcome parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new Outcome(List.of(), List.of("Empty file"));
        }
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename() == null
                ? ""
                : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        try {
            if (isXlsx(contentType, filename)) {
                return parseXlsx(file);
            }
            if (isCsv(contentType, filename)) {
                return parseCsv(file);
            }
            if (isDocx(contentType, filename)) {
                return parseDocx(file);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read upload", ex);
        }
        throw new UnsupportedOperationException(
                "Unsupported file type: " + (contentType == null ? filename : contentType));
    }

    // ── xlsx ─────────────────────────────────────────────────────────

    private Outcome parseXlsx(MultipartFile file) throws IOException {
        // Stream-only construction (skips OPCPackage which throws a
        // checked InvalidFormatException). XSSFWorkbook(InputStream) is
        // sufficient for the modest workbook sizes the wizard surfaces.
        try (InputStream in = file.getInputStream();
                Workbook wb = new XSSFWorkbook(in)) {
            if (wb.getNumberOfSheets() == 0) {
                return new Outcome(List.of(), List.of("Workbook has no sheets"));
            }
            // Sprint 4 contract — first sheet only. Buyers shouldn't be
            // splitting barcodes across tabs; if they do, the warning
            // captures the dropped-rows count.
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            List<String> raw = new ArrayList<>();
            int rowsSeen = 0;
            for (Row row : sheet) {
                ensureUnderCap(++rowsSeen);
                for (Cell cell : row) {
                    String value = fmt.formatCellValue(cell);
                    if (value != null && !value.isBlank()) {
                        ensureUnderCap(raw.size() + 1);
                        raw.add(value.trim());
                    }
                }
            }
            return classify(raw, rowsSeen);
        }
    }

    // ── csv ──────────────────────────────────────────────────────────

    private Outcome parseCsv(MultipartFile file) throws IOException {
        List<String> raw = new ArrayList<>();
        int rowsSeen = 0;
        try (InputStream in = file.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ensureUnderCap(++rowsSeen);
                // First column only — the Sprint 4 contract is
                // deliberately conservative; multi-column files force
                // the buyer to clean their data before upload.
                int sep = indexOfFirstSeparator(line);
                String first = sep < 0 ? line : line.substring(0, sep);
                first = first.trim();
                // Strip surrounding quotes if present — common Excel
                // export behaviour when a cell contains a comma.
                if (first.length() >= 2 && first.startsWith("\"") && first.endsWith("\"")) {
                    first = first.substring(1, first.length() - 1);
                }
                if (!first.isEmpty()) {
                    ensureUnderCap(raw.size() + 1);
                    raw.add(first);
                }
            }
        }
        return classify(raw, rowsSeen);
    }

    private static int indexOfFirstSeparator(String line) {
        int comma = line.indexOf(',');
        int tab = line.indexOf('\t');
        if (comma < 0) return tab;
        if (tab < 0) return comma;
        return Math.min(comma, tab);
    }

    // ── docx ─────────────────────────────────────────────────────────

    private Outcome parseDocx(MultipartFile file) throws IOException {
        List<String> raw = new ArrayList<>();
        int rowsSeen = 0;
        try (InputStream in = file.getInputStream();
                XWPFDocument doc = new XWPFDocument(in)) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                ensureUnderCap(++rowsSeen);
                String text = p.getText();
                if (text == null) continue;
                // Word docs frequently put one barcode per line but
                // also frequently smush them together with whitespace
                // — split on any whitespace to be forgiving.
                for (String token : text.split("\\s+")) {
                    String trimmed = token.trim();
                    if (!trimmed.isEmpty()) {
                        ensureUnderCap(raw.size() + 1);
                        raw.add(trimmed);
                    }
                }
            }
        }
        return classify(raw, rowsSeen);
    }

    // ── row / entry cap ──────────────────────────────────────────────

    /**
     * Rejects the upload once the running row/token count crosses
     * {@link #MAX_ENTRIES}. Called incrementally so a hostile file is
     * abandoned before the heap fills — not after a full read.
     */
    private static void ensureUnderCap(int count) {
        if (count > MAX_ENTRIES) {
            throw new TooManyRowsException(MAX_ENTRIES, count);
        }
    }

    // ── classification ───────────────────────────────────────────────

    /**
     * Apply the keep-rules + dedupe. Order is preserved (LinkedHashSet)
     * so the wizard renders rows in the order the buyer's source file
     * had them.
     */
    private Outcome classify(List<String> raw, int rowsSeen) {
        LinkedHashSet<String> kept = new LinkedHashSet<>();
        int dropped = 0;
        int duplicates = 0;
        for (String token : raw) {
            if (looksLikeBarcode(token)) {
                if (!kept.add(token)) {
                    duplicates++;
                }
            } else {
                dropped++;
            }
        }
        List<String> warnings = new ArrayList<>();
        if (dropped > 0) {
            warnings.add("Skipped " + dropped + " value(s) that didn't look like barcodes");
        }
        if (duplicates > 0) {
            warnings.add("Removed " + duplicates + " duplicate value(s)");
        }
        if (kept.isEmpty() && rowsSeen > 0) {
            warnings.add("No barcodes detected in the file");
        }
        return new Outcome(new ArrayList<>(kept), warnings);
    }

    static boolean looksLikeBarcode(String token) {
        if (token == null) return false;
        String s = token.trim();
        if (s.isEmpty()) return false;
        // Pure-digit short codes (< MIN_DIGIT_RUN_LENGTH) are noise —
        // row numbers, quantities, etc.
        if (s.chars().allMatch(Character::isDigit)) {
            return s.length() >= MIN_DIGIT_RUN_LENGTH;
        }
        // Mixed letters + digits → likely an IMEI / model code / etc.
        if (MIXED_ALNUM.matcher(s).matches()) {
            return true;
        }
        return false;
    }

    // ── content-type / filename detection ────────────────────────────

    private static boolean isXlsx(String contentType, String filename) {
        if (contentType != null
                && contentType.equalsIgnoreCase(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        }
        return filename.endsWith(".xlsx");
    }

    private static boolean isCsv(String contentType, String filename) {
        if (contentType != null
                && (contentType.equalsIgnoreCase("text/csv")
                        || contentType.equalsIgnoreCase("text/plain")
                        || contentType.equalsIgnoreCase("application/vnd.ms-excel"))) {
            return filename.isEmpty() || filename.endsWith(".csv") || filename.endsWith(".txt");
        }
        return filename.endsWith(".csv") || filename.endsWith(".txt");
    }

    private static boolean isDocx(String contentType, String filename) {
        if (contentType != null
                && contentType.equalsIgnoreCase(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return true;
        }
        return filename.endsWith(".docx");
    }
}
