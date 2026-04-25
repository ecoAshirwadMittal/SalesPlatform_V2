package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PODetailRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class POExcelBuilder {

    private static final String[] HEADERS =
            {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"};

    public void write(List<PODetailRow> rows, OutputStream out) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("PO");
            CellStyle bold = wb.createCellStyle();
            Font font = wb.createFont(); font.setBold(true);
            bold.setFont(font);

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(HEADERS[i]);
                c.setCellStyle(bold);
            }
            int r = 1;
            for (PODetailRow row : rows) {
                Row out2 = sheet.createRow(r++);
                out2.createCell(0).setCellValue(row.productId());
                out2.createCell(1).setCellValue(row.grade());
                out2.createCell(2).setCellValue(row.modelName() == null ? "" : row.modelName());
                out2.createCell(3).setCellValue(
                        row.price() == null ? 0d : row.price().doubleValue());
                if (row.qtyCap() != null) out2.createCell(4).setCellValue(row.qtyCap());
                out2.createCell(5).setCellValue(row.buyerCode() == null ? "" : row.buyerCode());
            }
            for (int i = 0; i < HEADERS.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
        } catch (IOException ex) {
            throw new PurchaseOrderException("EXPORT_FAILED",
                    "Failed to build PO Excel: " + ex.getMessage());
        }
    }
}
