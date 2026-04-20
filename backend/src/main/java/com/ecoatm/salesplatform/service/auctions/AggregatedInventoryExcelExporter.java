package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class AggregatedInventoryExcelExporter {

    private static final String[] HEADERS = {
            "Product ID", "Grades", "Brand", "Model", "Model Name", "Carrier",
            "DW Qty", "DW Target Price", "Total Qty", "Target Price"
    };

    public void write(List<AggregatedInventoryRow> rows, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Aggregated Inventory");

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) header.createCell(i).setCellValue(HEADERS[i]);

            int r = 1;
            for (var row : rows) {
                Row xr = sheet.createRow(r++);
                xr.createCell(0).setCellValue(row.ecoid2());
                xr.createCell(1).setCellValue(nullToEmpty(row.mergedGrade()));
                xr.createCell(2).setCellValue(nullToEmpty(row.brand()));
                xr.createCell(3).setCellValue(nullToEmpty(row.model()));
                xr.createCell(4).setCellValue(nullToEmpty(row.name()));
                xr.createCell(5).setCellValue(nullToEmpty(row.carrier()));
                xr.createCell(6).setCellValue(row.dwTotalQuantity());
                xr.createCell(7).setCellValue(bdToDouble(row.dwAvgTargetPrice()));
                xr.createCell(8).setCellValue(row.totalQuantity());
                xr.createCell(9).setCellValue(bdToDouble(row.avgTargetPrice()));
            }
            wb.write(out);
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static double bdToDouble(BigDecimal b) { return b == null ? 0 : b.doubleValue(); }
}
