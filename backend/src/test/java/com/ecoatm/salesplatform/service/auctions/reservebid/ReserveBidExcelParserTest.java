package com.ecoatm.salesplatform.service.auctions.reservebid;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReserveBidExcelParserTest {

    private final ReserveBidExcelParser parser = new ReserveBidExcelParser();

    @Test
    void parsesValidWorkbook() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price"},
                {"88001",      "A_YYY", "iPhone 12", "5.00"},
                {"88002",      "B_NNN", "iPhone 13", "7.25"}
        });
        var rows = parser.parse(new ByteArrayInputStream(bytes));
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).productId()).isEqualTo("88001");
        assertThat(rows.get(1).price()).isEqualByComparingTo("7.25");
    }

    @Test
    void tooManyDataRowsRejected() throws Exception {
        // Header at row 0 + a sparse row at index 10_001 makes getLastRowNum()
        // == 10_001 (> the 10_000 cap) without materialising 10k rows — H-9.
        byte[] bytes = makeWorkbookWithSparseLastRow(10_001);
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOfSatisfying(ReserveBidValidationException.class, ex ->
                        assertThat(ex.code()).isEqualTo("UPLOAD_ROW_LIMIT"));
    }

    private static byte[] makeWorkbook(String[][] rows) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ReserveBids");
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

    private static byte[] makeWorkbookWithSparseLastRow(int lastRowIndex) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("ReserveBids");
            Row headerRow = sheet.createRow(0);
            String[] header = {"ProductID", "Grade", "ModelName", "Price"};
            for (int c = 0; c < header.length; c++) {
                headerRow.createCell(c).setCellValue(header[c]);
            }
            sheet.createRow(lastRowIndex); // pushes getLastRowNum() past the cap
            wb.write(out);
            return out.toByteArray();
        }
    }
}
