package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class POExcelParserTest {

    private final POExcelParser parser = new POExcelParser();

    @Test
    void parsesValidWorkbook() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"12345",      "A_YYY", "iPhone 12",  "10.5",  "100",    "ABC"},
                {"12346",      "B_NNN", "iPhone 13",  "20",    "",       "DEF"}
        });
        var rows = parser.parse(new ByteArrayInputStream(bytes));
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).productId()).isEqualTo("12345");
        assertThat(rows.get(0).qtyCap()).isEqualTo(100);
        assertThat(rows.get(1).qtyCap()).isNull();
    }

    @Test
    void missingHeaderThrows() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price"},
                {"12345",      "A_YYY", "iPhone",     "10"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("UPLOAD_PARSE_ERROR"));
    }

    @Test
    void nonNumericPriceCollectsRowError() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"12345",      "A_YYY", "iPhone",     "BAD",   "10",     "ABC"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOfSatisfying(PurchaseOrderValidationException.class, ex -> {
                    assertThat(ex.getCode()).isEqualTo("UPLOAD_ROW_ERRORS");
                    assertThat(ex.getDetails()).anyMatch(s -> s.contains("Price"));
                });
    }

    @Test
    void emptyProductIdCollectsRowError() throws Exception {
        byte[] bytes = makeWorkbook(new String[][] {
                {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"},
                {"",           "A_YYY", "iPhone",     "10",    "100",    "ABC"}
        });
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(bytes)))
                .isInstanceOf(PurchaseOrderValidationException.class);
    }

    private static byte[] makeWorkbook(String[][] rows) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("PO");
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
}
