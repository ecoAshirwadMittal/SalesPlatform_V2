package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatedInventoryExcelExporterTest {

    @Test
    @DisplayName("writeWorkbook produces a workbook with header row and data rows")
    void writeWorkbook_hasHeaderAndData() throws Exception {
        var rows = List.of(new AggregatedInventoryRow(
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, BigDecimal.ZERO, 7, new BigDecimal("2.07"),
                false));

        var out = new ByteArrayOutputStream();
        new AggregatedInventoryExcelExporter().write(rows, out);

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(out.toByteArray()))) {
            var sheet = wb.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Product ID");
            assertThat(sheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Brand");
            assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).isEqualTo("75");
            assertThat(sheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("iPhone 3G");
            assertThat(sheet.getRow(1).getCell(8).getNumericCellValue()).isEqualTo(7);
        }
    }
}
