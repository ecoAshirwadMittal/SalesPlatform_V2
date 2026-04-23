package com.ecoatm.salesplatform.service.auctions.biddata;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BidExportService}.
 *
 * <p>Uses lenient stubbing to avoid Mockito's strict-mode vararg argument
 * mismatch detection — the test verifies xlsx output, not which SQL was run.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BidExportService")
class BidExportServiceTest {

    @Mock
    private JdbcTemplate jdbc;

    private BidExportService service;

    @BeforeEach
    void setUp() {
        service = new BidExportService(jdbc);
    }

    // ---------------------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("happy path: produces xlsx with header + data rows, all 12 columns present")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void export_happyPath_producesXlsx() throws Exception {
        List<Object[]> dbRows = new ArrayList<>();
        dbRows.add(new Object[]{
                1001L,                   // 0  bid_id
                "SKU-001",               // 1  ecoid
                "Apple",                 // 2  brand
                "iPhone 14",             // 3  model
                "iPhone 14 Pro",         // 4  model_name
                "Good",                  // 5  merged_grade
                "AT&T",                  // 6  carrier
                500,                     // 7  added
                120,                     // 8  maximum_quantity
                new BigDecimal("42.17"), // 9  target_price
                new BigDecimal("40.00"), // 10 bid_amount
                12                       // 11 bid_quantity
        });
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(dbRows);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.export(1L, 50L, out);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))) {
            Sheet sheet = wb.getSheetAt(0);

            // Header row
            Row header = sheet.getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Product Id");
            assertThat(header.getCell(9).getStringCellValue()).isEqualTo("Price");
            assertThat(header.getCell(10).getStringCellValue()).isEqualTo("Qty. Cap");
            assertThat(header.getCell(11).getStringCellValue()).isEqualTo("Id");

            // Data row
            Row data = sheet.getRow(1);
            assertThat(data).isNotNull();
            assertThat(data.getCell(0).getStringCellValue()).isEqualTo("SKU-001");
            assertThat(data.getCell(1).getStringCellValue()).isEqualTo("Apple");
            assertThat(data.getCell(4).getStringCellValue()).isEqualTo("Good");
            assertThat(data.getCell(9).getNumericCellValue()).isEqualTo(40.0);
            assertThat((int) data.getCell(10).getNumericCellValue()).isEqualTo(12);
            assertThat((long) data.getCell(11).getNumericCellValue()).isEqualTo(1001L);
        }
    }

    @Test
    @DisplayName("null bid_quantity produces empty Qty. Cap cell (no-cap sentinel)")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void export_nullBidQuantity_emptyQtyCap() throws Exception {
        List<Object[]> dbRows = new ArrayList<>();
        dbRows.add(new Object[]{
                2002L,
                "SKU-002",
                "Samsung",
                "Galaxy S24",
                "Galaxy S24 Ultra",
                "Fair",
                "Unlocked",
                200,
                50,
                new BigDecimal("30.00"),
                new BigDecimal("28.00"),
                null  // no cap
        });
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(dbRows);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.export(1L, 50L, out);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))) {
            Row data = wb.getSheetAt(0).getRow(1);
            assertThat(data).isNotNull();
            // null bid_quantity → blank string cell
            assertThat(data.getCell(10).getStringCellValue()).isEmpty();
        }
    }

    @Test
    @DisplayName("empty bid slice produces xlsx with header row only")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void export_emptySlice_headerOnly() throws Exception {
        List<Object[]> empty = new ArrayList<>();
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(empty);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.export(99L, 50L, out);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))) {
            Sheet sheet = wb.getSheetAt(0);
            // lastRowNum == 0 means only the header row exists
            assertThat(sheet.getLastRowNum()).isEqualTo(0);
        }
    }
}
