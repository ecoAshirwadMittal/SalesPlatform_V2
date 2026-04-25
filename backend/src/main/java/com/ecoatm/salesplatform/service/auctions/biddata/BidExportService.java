package com.ecoatm.salesplatform.service.auctions.biddata;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Streams a {@code .xlsx} export of the current bid-data slice for a given
 * {@code (bid_round, buyer_code)} pair.
 *
 * <p>Strategy (a) from the Phase 10 spec: the export query joins
 * {@code auctions.bid_data} → {@code auctions.aggregated_inventory} →
 * {@code mdm.brand / mdm.model / mdm.carrier} to populate the five columns
 * that the DTO currently lacks (brand, model, modelName, carrier, added).
 * The join is straightforward because {@code aggregated_inventory} already
 * carries denormalized text values alongside the FK columns.
 *
 * <p>Column order matches the grid exactly:
 * <ol>
 *   <li>Product Id (ecoid)</li>
 *   <li>Brand</li>
 *   <li>Model</li>
 *   <li>Model Name</li>
 *   <li>Grade</li>
 *   <li>Carrier</li>
 *   <li>Added (aggregated_inventory.total_quantity, the "Available" pool)</li>
 *   <li>Avail. Qty (maximum_quantity)</li>
 *   <li>Target Price</li>
 *   <li>Price (bid_amount)</li>
 *   <li>Qty. Cap (bid_quantity — NULL = no cap)</li>
 *   <li>Id (hidden, needed for import round-trip)</li>
 * </ol>
 *
 * <p>Uses {@link SXSSFWorkbook} (streaming) so a 500-row export never
 * materializes more than 100 rows in memory at once.
 */
@Service
public class BidExportService {

    private static final String[] HEADERS = {
            "Product Id", "Brand", "Model", "Model Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price",
            "Qty. Cap", "Id"
    };

    /**
     * Single-pass query: bid_data → aggregated_inventory → brand/model/carrier.
     * Ordered by ecoid, merged_grade for deterministic output (matches the grid
     * sort order).
     */
    private static final String EXPORT_SQL = """
            SELECT
                bd.id                            AS bid_id,
                bd.ecoid,
                COALESCE(ai.brand,  mb.name)        AS brand,
                COALESCE(ai.model,  mm.name)        AS model,
                ai.name                          AS model_name,
                bd.merged_grade,
                COALESCE(ai.carrier, mc.name)       AS carrier,
                ai.created_date                  AS added,
                bd.maximum_quantity,
                bd.target_price,
                bd.bid_amount,
                bd.bid_quantity
            FROM auctions.bid_data bd
            LEFT JOIN auctions.aggregated_inventory ai ON ai.id = bd.aggregated_inventory_id
            LEFT JOIN mdm.brand   mb ON mb.id = ai.brand_id
            LEFT JOIN mdm.model   mm ON mm.id = ai.model_id
            LEFT JOIN mdm.carrier mc ON mc.id = ai.carrier_id
            WHERE bd.bid_round_id = ? AND bd.buyer_code_id = ?
            ORDER BY bd.ecoid, bd.merged_grade
            """;

    private final JdbcTemplate jdbc;

    public BidExportService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Writes the xlsx stream to {@code out}. The caller is responsible for
     * setting the {@code Content-Disposition} and {@code Content-Type} headers
     * before invoking this method.
     *
     * @param bidRoundId   id of the bid round whose slice to export
     * @param buyerCodeId  buyer code — limits the export to this buyer's rows
     * @param out          response output stream; written and flushed here
     */
    @Transactional(readOnly = true)
    public void export(long bidRoundId, long buyerCodeId, OutputStream out) throws IOException {
        List<Object[]> rows = jdbc.query(
                EXPORT_SQL,
                (rs, rowNum) -> mapRow(rs),
                bidRoundId, buyerCodeId);

        // SXSSFWorkbook keeps only 100 rows in memory; the rest are spooled to a
        // temp file and streamed on write. Suitable for 500–2000 row exports.
        try (SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
            Sheet sheet = wb.createSheet("Bids");

            CellStyle headerStyle = buildHeaderStyle(wb);

            // --- Header row ---
            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < HEADERS.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(HEADERS[c]);
                cell.setCellStyle(headerStyle);
            }

            // --- Data rows ---
            int rowIdx = 1;
            for (Object[] data : rows) {
                Row row = sheet.createRow(rowIdx++);
                // ecoid
                setCellString(row, 0, (String) data[1]);
                // brand
                setCellString(row, 1, (String) data[2]);
                // model
                setCellString(row, 2, (String) data[3]);
                // model_name
                setCellString(row, 3, (String) data[4]);
                // grade
                setCellString(row, 4, (String) data[5]);
                // carrier
                setCellString(row, 5, (String) data[6]);
                // added — ai.created_date (Timestamp) formatted M/D/YYYY
                setCellString(row, 6, formatAdded((java.sql.Timestamp) data[7]));
                // avail. qty
                setCellInt(row, 7, toInt(data[8]));
                // target price
                setCellDecimal(row, 8, (BigDecimal) data[9]);
                // bid_amount (Price)
                setCellDecimal(row, 9, (BigDecimal) data[10]);
                // bid_quantity (Qty. Cap; null = no cap)
                if (data[11] != null) {
                    row.createCell(10).setCellValue((int) data[11]);
                } else {
                    row.createCell(10).setCellValue("");
                }
                // Id (hidden import key)
                row.createCell(11).setCellValue((Long) data[0]);
            }

            wb.write(out);
            // SXSSFWorkbook cleans up its temp file on dispose (called by try-with-resources)
        }
    }

    // ---------------------------------------------------------------------------
    // JDBC row mapper
    // ---------------------------------------------------------------------------

    private static Object[] mapRow(ResultSet rs) throws SQLException {
        return new Object[]{
                rs.getLong("bid_id"),       // 0  Long
                rs.getString("ecoid"),       // 1
                rs.getString("brand"),       // 2
                rs.getString("model"),       // 3
                rs.getString("model_name"),  // 4
                rs.getString("merged_grade"),// 5
                rs.getString("carrier"),     // 6
                rs.getTimestamp("added"),    // 7  Timestamp (nullable — formatted in sheet writer)
                rs.getObject("maximum_quantity", Integer.class), // 8 nullable
                rs.getObject("target_price", BigDecimal.class),  // 9 nullable
                rs.getObject("bid_amount", BigDecimal.class),    // 10 nullable
                rs.getObject("bid_quantity", Integer.class)      // 11 nullable
        };
    }

    // ---------------------------------------------------------------------------
    // Cell helpers
    // ---------------------------------------------------------------------------

    private static void setCellString(Row row, int col, String value) {
        row.createCell(col).setCellValue(value != null ? value : "");
    }

    /**
     * Formats a {@code Timestamp} as {@code M/D/YYYY} — QA parity for the
     * Added column. Returns empty string for null.
     */
    private static String formatAdded(java.sql.Timestamp ts) {
        if (ts == null) return "";
        java.time.LocalDate d = ts.toLocalDateTime().toLocalDate();
        return d.getMonthValue() + "/" + d.getDayOfMonth() + "/" + d.getYear();
    }

    private static void setCellInt(Row row, int col, int value) {
        row.createCell(col).setCellValue(value);
    }

    private static void setCellDecimal(Row row, int col, BigDecimal value) {
        if (value != null) {
            row.createCell(col).setCellValue(value.doubleValue());
        } else {
            row.createCell(col).setCellValue(0.0);
        }
    }

    private static int toInt(Object obj) {
        if (obj instanceof Number n) return n.intValue();
        return 0;
    }

    // ---------------------------------------------------------------------------
    // Style helpers
    // ---------------------------------------------------------------------------

    private static CellStyle buildHeaderStyle(SXSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }
}
