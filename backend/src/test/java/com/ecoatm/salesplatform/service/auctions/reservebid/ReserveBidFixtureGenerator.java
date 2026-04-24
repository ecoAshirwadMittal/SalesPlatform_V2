package com.ecoatm.salesplatform.service.auctions.reservebid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.file.Path;

public final class ReserveBidFixtureGenerator {

    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args[0]);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            header(s, "ProductID", "Grade", "ModelName", "Price");
            addRow(s, 1, 10001, "A_YYY", "Pixel 7",    50.25);
            addRow(s, 2, 10002, "A_YYY", "Pixel 8",    60.00);
            addRow(s, 3, 10003, "B_NYY", "Moto G",     25.75);
            try (FileOutputStream out = new FileOutputStream(dir.resolve("reserve-bid-sample.xlsx").toFile())) {
                wb.write(out);
            }
        }

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("ReserveBids");
            header(s, "ProductID", "Grade", "ModelName", "Price");
            addRow(s, 1, 20001, "A_YYY", "Good",  10.00);
            addRow(s, 2, 20001, "A_YYY", "DupSheet", 15.00);
            addRow(s, 3, 20002, "",      "MissingGrade", 20.00);
            addRow(s, 4, 20003, "B_NYY", "NegPrice",  -5.00);
            try (FileOutputStream out = new FileOutputStream(dir.resolve("reserve-bid-with-errors.xlsx").toFile())) {
                wb.write(out);
            }
        }
    }

    private static void header(Sheet s, String... names) {
        Row h = s.createRow(0);
        for (int i = 0; i < names.length; i++) h.createCell(i).setCellValue(names[i]);
    }

    private static void addRow(Sheet s, int rowIdx, int productId, String grade, String model, double price) {
        Row r = s.createRow(rowIdx);
        r.createCell(0).setCellValue(productId);
        r.createCell(1).setCellValue(grade);
        r.createCell(2).setCellValue(model);
        r.createCell(3).setCellValue(price);
    }
}
