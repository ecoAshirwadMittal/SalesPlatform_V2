package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class POExcelParser {

    public record ParsedRow(int rowNumber, String productId, String grade, String modelName,
                            BigDecimal price, Integer qtyCap, String buyerCode) {}

    private static final List<String> REQUIRED = List.of(
            "ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode");

    public List<ParsedRow> parse(InputStream in) {
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                        "Workbook contains no sheets", List.of());
            }
            Map<String, Integer> headerIndex = readHeader(sheet.getRow(0));
            List<ParsedRow> parsed = new ArrayList<>();
            List<String> rowErrors = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isBlankRow(row, headerIndex)) continue;
                try {
                    parsed.add(parseRow(r + 1, row, headerIndex));
                } catch (PurchaseOrderValidationException ex) {
                    rowErrors.add(ex.getMessage());
                }
            }
            if (!rowErrors.isEmpty()) {
                throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                        "Row-level validation errors (" + rowErrors.size() + ")", rowErrors);
            }
            return parsed;
        } catch (IOException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "Cannot read workbook: " + ex.getMessage(), List.of());
        }
    }

    private static Map<String, Integer> readHeader(Row header) {
        if (header == null) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "First row must be the header row", List.of());
        }
        Map<String, Integer> idx = new HashMap<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            Cell cell = header.getCell(c);
            if (cell == null) continue;
            idx.put(cell.getStringCellValue().trim(), c);
        }
        List<String> missing = REQUIRED.stream().filter(h -> !idx.containsKey(h)).toList();
        if (!missing.isEmpty()) {
            throw new PurchaseOrderValidationException("UPLOAD_PARSE_ERROR",
                    "Missing required columns: " + String.join(", ", missing),
                    missing);
        }
        return idx;
    }

    private static boolean isBlankRow(Row row, Map<String, Integer> idx) {
        for (Integer c : idx.values()) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !asString(cell).isBlank()) return false;
        }
        return true;
    }

    private static ParsedRow parseRow(int rowNumber, Row row, Map<String, Integer> idx) {
        String productId = asString(row.getCell(idx.get("ProductID"))).trim();
        if (productId.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": ProductID is empty", List.of());
        }
        String grade = asString(row.getCell(idx.get("Grade"))).trim();
        if (grade.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": Grade is empty", List.of());
        }
        String modelName = asString(row.getCell(idx.get("ModelName"))).trim();
        BigDecimal price = parseDecimal(row.getCell(idx.get("Price")), rowNumber, "Price");
        Integer qtyCap = parseOptionalInt(row.getCell(idx.get("QtyCap")), rowNumber, "QtyCap");
        String buyerCode = asString(row.getCell(idx.get("BuyerCode"))).trim();
        if (buyerCode.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": BuyerCode is empty", List.of());
        }
        return new ParsedRow(rowNumber, productId, grade, modelName, price, qtyCap, buyerCode);
    }

    private static String asString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                if (v == Math.floor(v)) yield String.valueOf((long) v);
                yield String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try { yield cell.getStringCellValue(); }
                catch (IllegalStateException ex) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    private static BigDecimal parseDecimal(Cell cell, int rowNumber, String field) {
        String raw = asString(cell).trim();
        if (raw.isBlank()) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " is empty", List.of());
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " not numeric: " + raw, List.of());
        }
    }

    private static Integer parseOptionalInt(Cell cell, int rowNumber, String field) {
        String raw = asString(cell).trim();
        if (raw.isBlank()) return null;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new PurchaseOrderValidationException("UPLOAD_ROW_ERRORS",
                    "Row " + rowNumber + ": " + field + " not integer: " + raw, List.of());
        }
    }
}
