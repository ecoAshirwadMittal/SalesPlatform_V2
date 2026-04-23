package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidImportResult;
import com.ecoatm.salesplatform.dto.BidImportRowError;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parses an xlsx bid sheet uploaded by a bidder, validates every row, and
 * applies the updates atomically — if any validation error is found the
 * entire import is rejected.
 *
 * <h2>Expected column layout (same as the export)</h2>
 * <pre>
 *  0  Product Id   (ecoid — ignored on import)
 *  1  Brand        (ignored)
 *  2  Model        (ignored)
 *  3  Model Name   (ignored)
 *  4  Grade        (ignored)
 *  5  Carrier      (ignored)
 *  6  Added        (ignored)
 *  7  Avail. Qty   (ignored)
 *  8  Target Price (ignored)
 *  9  Price        ← bid_amount
 * 10  Qty. Cap     ← bid_quantity (blank = no-cap sentinel, numeric = value)
 * 11  Id           ← bid_data.id (required, must be owned by this buyer/round)
 * </pre>
 *
 * <h2>Validation gates</h2>
 * <ol>
 *   <li>File must be a valid xlsx (magic bytes PK\x03\x04; content-type
 *       check in the controller).</li>
 *   <li>Size cap: {@literal ≤ 5 MB}.</li>
 *   <li>Row cap: {@literal ≤ 10 000} data rows.</li>
 *   <li>Per-row: id column must be a positive long; the id must belong to the
 *       {@code (bid_round_id, buyer_code_id)} slice; bidAmount ≥ 0 and
 *       {@literal ≤ 1_000_000}; bidQuantity is null OR a non-negative
 *       integer {@literal ≤ 999_999}.</li>
 * </ol>
 *
 * <h2>Apply</h2>
 * All rows are applied in a single {@code REQUIRES_NEW} transaction via JDBC
 * batch update. If any pre-apply validation fails the transaction never opens.
 */
@Service
public class BidImportService {

    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;   // 5 MB
    private static final int  MAX_DATA_ROWS  = 10_000;

    private static final BigDecimal MAX_BID_AMOUNT = new BigDecimal("1000000");
    private static final int        MAX_BID_QTY    = 999_999;

    /** Col indices in the exported sheet (0-based). */
    private static final int COL_PRICE   = 9;
    private static final int COL_QTY_CAP = 10;
    private static final int COL_ID      = 11;

    private static final String OWNERSHIP_CHECK_SQL = """
            SELECT COUNT(*) FROM auctions.bid_data
            WHERE id = ? AND bid_round_id = ? AND buyer_code_id = ?
            """;

    private static final String ROUND_STATUS_SQL = """
            SELECT sa.round_status
              FROM auctions.bid_rounds br
              JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
             WHERE br.id = ?
            """;

    private static final String UPDATE_ROW_SQL = """
            UPDATE auctions.bid_data
               SET bid_amount    = ?,
                   bid_quantity  = ?,
                   changed_date  = ?,
                   changed_by_id = ?
             WHERE id = ?
            """;

    private final JdbcTemplate jdbc;

    public BidImportService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Parse, validate, and apply the uploaded xlsx in one pass.
     *
     * @param userId      authenticated user performing the import
     * @param bidRoundId  bid round this import targets
     * @param buyerCodeId buyer code — scopes the ownership check
     * @param file        the uploaded xlsx multipart file
     * @return a {@link BidImportResult} with {@code updated} count and any
     *         per-row errors (non-empty = the import was fully rejected)
     * @throws BidDataSubmissionException with code {@code ROUND_CLOSED}  if
     *                                    the round is no longer open
     * @throws BidDataValidationException with code {@code IMPORT_INVALID} if
     *                                    the file is oversized or too many rows
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public BidImportResult importBids(long userId,
                                      long bidRoundId,
                                      long buyerCodeId,
                                      MultipartFile file) throws IOException {
        // --- Gate 1: file size ---
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new BidDataValidationException("IMPORT_INVALID",
                    "File exceeds 5 MB limit (" + file.getSize() + " bytes)");
        }

        // --- Gate 2: round must be open ---
        assertRoundOpen(bidRoundId);

        // --- Gate 3: parse + validate rows ---
        List<ParsedRow> parsed = parseSheet(file.getInputStream(), bidRoundId, buyerCodeId);

        // If any row has a validation error, return errors without applying.
        List<BidImportRowError> errors = collectErrors(parsed);
        if (!errors.isEmpty()) {
            return new BidImportResult(0, errors);
        }

        // --- Gate 4: apply atomically ---
        Instant now = Instant.now();
        int updated = applyRows(parsed, userId, now);
        return new BidImportResult(updated, List.of());
    }

    // ---------------------------------------------------------------------------
    // Parsing
    // ---------------------------------------------------------------------------

    private List<ParsedRow> parseSheet(InputStream in,
                                       long bidRoundId,
                                       long buyerCodeId) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            // Row 0 is the header; data starts at row 1.
            int dataRowCount = lastRow; // lastRowNum is 0-based, header = row 0
            if (dataRowCount > MAX_DATA_ROWS) {
                throw new BidDataValidationException("IMPORT_INVALID",
                        "File contains " + dataRowCount + " data rows; limit is " + MAX_DATA_ROWS);
            }

            // Pre-fetch the set of valid ids for this (round, buyer_code) slice so
            // we only need one round-trip to the DB instead of one per row.
            Set<Long> validIds = fetchValidIds(bidRoundId, buyerCodeId);

            List<ParsedRow> result = new ArrayList<>(dataRowCount);
            for (int rowIdx = 1; rowIdx <= lastRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue; // sparse xlsx — skip truly empty rows
                }
                result.add(validateRow(row, rowIdx + 1 /* 1-based for error reporting */, validIds));
            }
            return result;
        }
    }

    private ParsedRow validateRow(Row row, int displayRow, Set<Long> validIds) {
        // --- Id column (col 11) ---
        Long id = parseLong(row.getCell(COL_ID));
        if (id == null || id <= 0) {
            return ParsedRow.error(displayRow, "Id column is missing or not a valid number");
        }
        if (!validIds.contains(id)) {
            return ParsedRow.error(displayRow,
                    "Row id=" + id + " does not belong to this round / buyer code");
        }

        // --- Price (bid_amount, col 9) ---
        BigDecimal bidAmount = parseDecimal(row.getCell(COL_PRICE));
        if (bidAmount == null) {
            return ParsedRow.error(displayRow, "Price must be a number");
        }
        if (bidAmount.compareTo(BigDecimal.ZERO) < 0) {
            return ParsedRow.error(displayRow, "Price cannot be negative");
        }
        if (bidAmount.compareTo(MAX_BID_AMOUNT) > 0) {
            return ParsedRow.error(displayRow, "Price exceeds maximum allowed value of 1,000,000");
        }

        // --- Qty. Cap (bid_quantity, col 10) — blank = no-cap sentinel ---
        Integer bidQuantity = null;
        Cell qtyCell = row.getCell(COL_QTY_CAP);
        if (qtyCell != null && !isCellBlank(qtyCell)) {
            BigDecimal rawQty = parseDecimal(qtyCell);
            if (rawQty == null) {
                return ParsedRow.error(displayRow, "Qty. Cap must be a non-negative integer or blank");
            }
            // Reject decimals (e.g. 1.5)
            if (rawQty.stripTrailingZeros().scale() > 0) {
                return ParsedRow.error(displayRow, "Qty. Cap must be a whole number");
            }
            int qtyInt = rawQty.intValueExact();
            if (qtyInt < 0) {
                return ParsedRow.error(displayRow, "Qty. Cap cannot be negative");
            }
            if (qtyInt > MAX_BID_QTY) {
                return ParsedRow.error(displayRow,
                        "Qty. Cap exceeds maximum allowed value of " + MAX_BID_QTY);
            }
            bidQuantity = qtyInt;
        }

        return ParsedRow.valid(displayRow, id, bidAmount.setScale(2, RoundingMode.HALF_UP), bidQuantity);
    }

    // ---------------------------------------------------------------------------
    // DB helpers
    // ---------------------------------------------------------------------------

    private Set<Long> fetchValidIds(long bidRoundId, long buyerCodeId) {
        List<Long> ids = jdbc.queryForList(
                "SELECT id FROM auctions.bid_data WHERE bid_round_id = ? AND buyer_code_id = ?",
                Long.class, bidRoundId, buyerCodeId);
        return new HashSet<>(ids);
    }

    private void assertRoundOpen(long bidRoundId) {
        String status = jdbc.queryForObject(ROUND_STATUS_SQL, String.class, bidRoundId);
        if ("Closed".equals(status)) {
            throw new BidDataSubmissionException("ROUND_CLOSED", "Round is closed");
        }
    }

    private int applyRows(List<ParsedRow> rows, long userId, Instant now) {
        List<Object[]> batchArgs = new ArrayList<>(rows.size());
        for (ParsedRow r : rows) {
            batchArgs.add(new Object[]{
                    r.bidAmount(),
                    r.bidQuantity(),
                    now,
                    userId,
                    r.id()
            });
        }
        int[] counts = jdbc.batchUpdate(UPDATE_ROW_SQL, batchArgs);
        int total = 0;
        for (int c : counts) {
            total += c;
        }
        return total;
    }

    private static boolean hasAdministratorRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        for (var a : auth.getAuthorities()) {
            if ("ROLE_Administrator".equals(a.getAuthority())) return true;
        }
        return false;
    }

    // ---------------------------------------------------------------------------
    // Error aggregation
    // ---------------------------------------------------------------------------

    private static List<BidImportRowError> collectErrors(List<ParsedRow> rows) {
        List<BidImportRowError> errors = new ArrayList<>();
        for (ParsedRow r : rows) {
            if (r.error() != null) {
                errors.add(new BidImportRowError(r.displayRow(), r.error()));
            }
        }
        return errors;
    }

    // ---------------------------------------------------------------------------
    // Cell parsing helpers
    // ---------------------------------------------------------------------------

    private static BigDecimal parseDecimal(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            case FORMULA -> {
                try {
                    yield BigDecimal.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static Long parseLong(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static boolean isCellBlank(Cell cell) {
        if (cell == null) return true;
        if (cell.getCellType() == CellType.BLANK) return true;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().isBlank();
        return false;
    }

    // ---------------------------------------------------------------------------
    // ParsedRow value type
    // ---------------------------------------------------------------------------

    /**
     * Represents a single parsed data row from the xlsx.
     * Either carries valid data ({@link #error()} == null) or an error message.
     */
    private record ParsedRow(
            int displayRow,
            Long id,
            BigDecimal bidAmount,
            Integer bidQuantity,
            String error) {

        static ParsedRow valid(int displayRow, long id, BigDecimal bidAmount, Integer bidQuantity) {
            return new ParsedRow(displayRow, id, bidAmount, bidQuantity, null);
        }

        static ParsedRow error(int displayRow, String message) {
            return new ParsedRow(displayRow, null, null, null, message);
        }
    }
}
