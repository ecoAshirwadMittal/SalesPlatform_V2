package com.ecoatm.salesplatform.service.auctions.reservebid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReserveBidExcelParser {

    public record ParsedRow(int rowNumber, String productId, String grade,
                            String modelName, BigDecimal price) {}

    /**
     * Hard cap on data rows, mirroring {@code BidImportService.MAX_DATA_ROWS}
     * (security review 2026-07-10, H-9). Bounds the heap a hostile workbook can
     * force POI to allocate before validation. Row 0 is the header, so
     * {@code getLastRowNum()} (0-based) is the data-row count.
     */
    private static final int MAX_DATA_ROWS = 10_000;

    public List<ParsedRow> parse(InputStream in) {
        List<ParsedRow> rows = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            int last = sheet.getLastRowNum();
            if (last > MAX_DATA_ROWS) {
                throw new ReserveBidValidationException("UPLOAD_ROW_LIMIT",
                        "File contains " + last + " data rows; limit is " + MAX_DATA_ROWS);
            }
            for (int i = 1; i <= last; i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                rows.add(new ParsedRow(
                        i + 1,
                        cellString(r.getCell(0)),
                        cellString(r.getCell(1)),
                        cellString(r.getCell(2)),
                        cellDecimal(r.getCell(3))));
            }
        } catch (IOException ex) {
            throw new ReserveBidValidationException("UPLOAD_PARSE_ERROR",
                    "Failed to read Excel: " + ex.getMessage());
        }
        return rows;
    }

    private static String cellString(Cell c) {
        if (c == null) return null;
        return switch (c.getCellType()) {
            case STRING  -> c.getStringCellValue();
            case NUMERIC -> new BigDecimal(c.getNumericCellValue()).toPlainString();
            case BLANK   -> null;
            default      -> c.toString();
        };
    }

    private static BigDecimal cellDecimal(Cell c) {
        if (c == null) return null;
        return switch (c.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(c.getNumericCellValue());
            case STRING  -> { try { yield new BigDecimal(c.getStringCellValue()); } catch (NumberFormatException e) { yield null; } }
            default      -> null;
        };
    }
}
