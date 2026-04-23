package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class ReserveBidExcelWriter {

    private static final String[] HEADERS = {
            "ProductID", "Grade", "Brand", "Model", "Bid",
            "LastUpdateDatetime", "LastAwardedMinPrice", "LastAwardedWeek"
    };

    public void writeAll(List<ReserveBid> rows, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            Row header = s.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) header.createCell(i).setCellValue(HEADERS[i]);
            for (int i = 0; i < rows.size(); i++) {
                ReserveBid rb = rows.get(i);
                Row r = s.createRow(i + 1);
                r.createCell(0).setCellValue(rb.getProductId());
                r.createCell(1).setCellValue(rb.getGrade());
                if (rb.getBrand() != null) r.createCell(2).setCellValue(rb.getBrand());
                if (rb.getModel() != null) r.createCell(3).setCellValue(rb.getModel());
                r.createCell(4).setCellValue(rb.getBid() != null ? rb.getBid().doubleValue() : 0);
                if (rb.getLastUpdateDatetime() != null)
                    r.createCell(5).setCellValue(rb.getLastUpdateDatetime().toString());
                if (rb.getLastAwardedMinPrice() != null)
                    r.createCell(6).setCellValue(rb.getLastAwardedMinPrice().doubleValue());
                if (rb.getLastAwardedWeek() != null) r.createCell(7).setCellValue(rb.getLastAwardedWeek());
            }
            wb.write(out);
        }
    }
}
