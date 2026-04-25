package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 * One-off helper to (re)generate {@code po-upload-sample.xlsx}.
 *
 * <p>Usage: {@code mvn -pl backend test-compile exec:java
 *   -Dexec.classpathScope=test
 *   -Dexec.mainClass=com.ecoatm.salesplatform.service.auctions.purchaseorder.POFixtureGenerator
 *   -Dexec.args=backend/src/test/resources/fixtures}
 *
 * <p>The generated workbook has a header row plus 20 unique
 * {@code (ProductID, Grade, BuyerCode)} triples — enough to exercise
 * {@link POExcelParser} without tripping the {@code DUPLICATE_IN_SHEET}
 * branch in {@link PODetailService}. Buyer-code values mirror real codes
 * seen in {@code buyer_mgmt.buyer_codes} (e.g. {@code NB_PWS}, {@code NB_GAZ})
 * but the slice IT mocks the service layer, so they never need to resolve
 * to live DB rows in unit/integration runs.
 */
public final class POFixtureGenerator {

    private static final String[] BUYER_CODES =
            {"NB_PWS", "NB_GAZ", "NB_DLV", "NB_BLD", "NB_RGN"};
    private static final String[] GRADES = {"A_YYY", "B_NYY", "C_NNY", "D_NNN"};

    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args.length > 0 ? args[0]
                : "backend/src/test/resources/fixtures");

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("PO");
            Row header = s.createRow(0);
            String[] cols = {"ProductID", "Grade", "ModelName", "Price", "QtyCap", "BuyerCode"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

            // 5 buyer codes x 4 grades = 20 unique (productId, grade, buyerCode) triples.
            // ProductID encodes the BC index so each row is also unique on (ProductID, Grade).
            int rowIdx = 1;
            for (int bc = 0; bc < BUYER_CODES.length; bc++) {
                for (int g = 0; g < GRADES.length; g++) {
                    Row r = s.createRow(rowIdx++);
                    r.createCell(0).setCellValue(80000 + bc * 10 + g);
                    r.createCell(1).setCellValue(GRADES[g]);
                    r.createCell(2).setCellValue("Pixel Sample " + bc + "-" + g);
                    r.createCell(3).setCellValue(50.0d + bc * 5 + g);
                    r.createCell(4).setCellValue(100 + bc * 10 + g);
                    r.createCell(5).setCellValue(BUYER_CODES[bc]);
                }
            }
            try (FileOutputStream out = new FileOutputStream(
                    dir.resolve("po-upload-sample.xlsx").toFile())) {
                wb.write(out);
            }
        }
        System.out.println("Wrote po-upload-sample.xlsx to " + dir.toAbsolutePath());
    }

    private POFixtureGenerator() {}
}
