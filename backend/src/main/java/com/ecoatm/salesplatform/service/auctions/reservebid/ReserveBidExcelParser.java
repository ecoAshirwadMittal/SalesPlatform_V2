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

    public List<ParsedRow> parse(InputStream in) {
        List<ParsedRow> rows = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            int last = sheet.getLastRowNum();
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
