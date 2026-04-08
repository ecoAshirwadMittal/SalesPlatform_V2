# Utility: data_utils.ts

- **Path**: `src\utils\helpers\data_utils.ts`
- **Category**: Utility
- **Lines**: 553
- **Size**: 21,528 bytes
- **Members**: `downloadFile`, `clickLocatorAnddownloadFile`, `modifyBidsExcelSheet`, `uploadFile`, `excelToCSV`, `Logger`, `readInventoryExcel`, `getAllTargetPricesFromExcel`, `readCaseLotExcel`, `readRMAExcel`

## Source Code

```typescript
import { Page, expect, Locator } from "playwright/test";
import * as path from 'path';
import fs from 'fs';
import XLSX from 'xlsx';
import csvParser from "csv-parser";
import { error, trace } from "console";
import pino from "pino";

const __dirTestData = "./src/test-data/";

export async function validateDuplication(page: Page, downloadButtonXpath: string, nameFile_excel: string): Promise<boolean> {
    const file = await downloadFile(page, downloadButtonXpath, nameFile_excel);
    expect(await isDownloaded(nameFile_excel)).toBe(true);
    const nameFile_csv = nameFile_excel.replace(/\.xlsx$/, '.csv');
    await excelToCSV(nameFile_excel, nameFile_csv);
    const duplicated: boolean = await isItemDuplicate_CSV(nameFile_csv);
    return !duplicated; // true if no duplicates, false if duplicates found
}

export async function downloadFile(page: Page, xpath: string, fileName: string) {
    if (!fs.existsSync(__dirTestData)) {
        fs.mkdirSync(__dirTestData, { recursive: true });
    }
    const [download] = await Promise.all([
        page.waitForEvent("download"),
        page.locator(xpath).click(),
    ]);
    const filePath = path.join(__dirTestData, fileName);
    await download.saveAs(filePath);
}

export async function clickLocatorAnddownloadFile(page: Page, locator: Locator, fileName: string) {
    if (!fs.existsSync(__dirTestData)) {
        fs.mkdirSync(__dirTestData, { recursive: true });
    }
    const [download] = await Promise.all([
        page.waitForEvent("download"),
        locator.click(),
    ]);
    const filePath = path.join(__dirTestData, fileName);
    await download.saveAs(filePath);
}

export async function isDownloaded(expectedFileName: string): Promise<boolean> {
    const filePath = path.join(__dirTestData, expectedFileName);
    const fileExists = fs.existsSync(filePath);
    return fileExists;
}

export async function modifyBidsExcelSheet(fileName: string, modifyBids: Array<{ productID: string, grade: string, bidPrice: string, bidQty: string }>) {
    const filePath = path.join(__dirTestData, fileName);
    if (!fs.existsSync(filePath)) {
        throw new Error("File Not Found: " + filePath);
    }
    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const range = XLSX.utils.decode_range(sheet["!ref"]!);
    // Find header column indices
    const headerMap: Record<string, number> = {};
    for (let C = range.s.c; C <= range.e.c; C++) {
        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: C });
        const cell = sheet[cellAddress];
        if (cell) headerMap[cell.v] = C;
    }
    for (const eachBid of modifyBids) {
        let rowFound = false;
        for (let R = 1; R <= range.e.r; R++) {
            const productCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["ProductId"] })];
            const gradeCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Grade"] })];
            if (!productCell || !gradeCell) continue;
            if (productCell.v == eachBid.productID && gradeCell.v == eachBid.grade) {
                // Update price + qty
                sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Price"] })] = { t: "n", v: Number(eachBid.bidPrice) };
                sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Qty. Cap"] })] = { t: "n", v: Number(eachBid.bidQty) };
                rowFound = true;
                break;
            }
        }
        if (!rowFound) {
            throw new Error(`ID + Grade Not Found: ${eachBid.productID} ${eachBid.grade}`);
        }
    }
    XLSX.writeFileXLSX(workbook, filePath, {
        compression: true,
        bookType: "xlsx",
        //dense: true
    });
}

export async function uploadFile(page: Page, inputFieldXpath: string, fileName: string) {
    const filePath = path.join(__dirTestData, fileName)
    if (!fs.existsSync(filePath)) {
        throw new Error("File Not Found: " + filePath);
    }
    const inputLocator = page.locator(inputFieldXpath);
    await expect(inputLocator).toBeVisible({ timeout: 5000 });
    await expect(inputLocator).toBeEnabled({ timeout: 5000 });
    for (let i = 1; i <= 3; i++) {
        try {
            await inputLocator.setInputFiles(filePath);
            break;
        } catch (error) {
            console.error(`Upload attempt ${i} FAILED: `, error);
            if (i == 3) {
                throw new Error("Failed to upload file after 3 attempts");
            }
        }
    }
}

export async function excelToCSV(excelFileName: string, csvFileName: string) {
    const csvPath = path.join(__dirTestData, csvFileName);
    const excelPath = path.join(__dirTestData, excelFileName)
    try {
        await fs.promises.mkdir(__dirTestData, { recursive: true });
        if (!fs.existsSync(excelPath)) {
            throw new Error("Error: File Not Found at Path: " + excelPath);
        }
        const workbook = XLSX.readFile(excelPath);
        const sheetName = workbook.SheetNames[0];
        const csvData = XLSX.utils.sheet_to_csv(workbook.Sheets[sheetName]);
        await fs.promises.writeFile(csvPath, csvData, "utf8");
    } catch (error) {
        console.error("Error during excel to CSV conversion ", error);
    }
}

export async function isItemDuplicate_CSV(csvFileName: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
        const seenProduct = new Set();
        let isDuplicateFound = false;
        const csvFilePath = path.join(__dirTestData, csvFileName)
        fs.createReadStream(csvFilePath).pipe(csvParser())
            .on("data", (row) => {
                const productId = row["Product ID"]?.trim() || row["ProductId"]?.trim();
                const grade = row["Grades"] || row["Grade"];
                if (!productId || !grade) {
                    console.error("Missing ProductID or Grade in row: " + error);
                }
                const productKey = productId + grade;
                if (seenProduct.has(productKey)) {
                    console.error("Duplicate Found: ProductID = " + productId + ", Grade = " + grade);
                    isDuplicateFound = true;
                } else {
                    seenProduct.add(productKey);
                }
            })
            .on("end", () => {
                console.log("CSV Validation Complete.");
                resolve(isDuplicateFound);
            })
            .on("error", (error) => {
                console.log("Error Reading CSV File: " + error);
                reject(error);
            });
    });
}

export const logger = pino({
    level: "info",
    transport: {
        target: 'pino-pretty',
        options: {
            colorize: true,
            translateTime: "HH:MM:ss Z",
            ignore: "pid, hostname"
        }
    }
})

export function Logger(message: string) {
    logger.info(message);
}

/**
 * Inventory row data structure matching Excel and table columns
 */
export interface InventoryRowData {
    sku: string;
    category: string;
    brand: string;
    model: string;      // "Model Family" in Excel
    carrier: string;
    capacity: string;
    color: string;
    grade: string;
    avlQty: number;     // "Avl. Qty" in Excel
    price: number;      // "Price" in Excel
}

/**
 * Read inventory Excel file and parse it into structured data
 * Excel headers: SKU, Category, Brand, Model Family, Carrier, Capacity, Color, Grade, Avl. Qty, Price
 * @param filePath Path to the Excel file
 * @returns Array of row data objects
 */
export function readInventoryExcel(filePath: string): InventoryRowData[] {
    if (!fs.existsSync(filePath)) {
        throw new Error(`Excel file not found: ${filePath}`);
    }
    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    // Convert sheet to JSON with headers
    const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as any[][];

    if (rows.length < 2) {
        Logger("Excel file has no data rows");
        return [];
    }
    // Exact header mapping based on known Excel structure
    // Headers: SKU, Category, Brand, Model Family, Carrier, Capacity, Color, Grade, Avl. Qty, Price
    const EXCEL_COLUMNS = {
        SKU: 0,
        CATEGORY: 1,
        BRAND: 2,
        MODEL: 3,        // "Model Family"
        CARRIER: 4,
        CAPACITY: 5,
        COLOR: 6,
        GRADE: 7,
        AVL_QTY: 8,      // "Avl. Qty"
        PRICE: 9         // "Price"
    };
    Logger(`Excel headers: ${JSON.stringify(rows[0])}`);
    const data: InventoryRowData[] = [];
    // Parse data rows (skip header row)
    for (let i = 1; i < rows.length; i++) {
        const row = rows[i];
        if (!row || row.length === 0) continue;
        const sku = (row[EXCEL_COLUMNS.SKU] || '').toString().trim();
        if (!sku) continue;
        const priceVal = row[EXCEL_COLUMNS.PRICE];
        const price = parseFloat(String(priceVal || '0').replace('$', '').replace(',', '')) || 0;
        const qtyVal = row[EXCEL_COLUMNS.AVL_QTY];
        const qtyStr = String(qtyVal || '0').trim();
        const avlQty = qtyStr === '100+' ? 100 : parseInt(qtyStr.replace(',', '')) || 0;
        data.push({
            sku,
            category: (row[EXCEL_COLUMNS.CATEGORY] || '').toString().trim(),
            brand: (row[EXCEL_COLUMNS.BRAND] || '').toString().trim(),
            model: (row[EXCEL_COLUMNS.MODEL] || '').toString().trim(),
            carrier: (row[EXCEL_COLUMNS.CARRIER] || '').toString().trim(),
            capacity: (row[EXCEL_COLUMNS.CAPACITY] || '').toString().trim(),
            color: (row[EXCEL_COLUMNS.COLOR] || '').toString().trim(),
            grade: (row[EXCEL_COLUMNS.GRADE] || '').toString().trim(),
            avlQty,
            price
        });
    }

    Logger(`Parsed ${data.length} rows from Excel file`);
    return data;
}

export function validateExcelColumns(fileName: string): { headers: string[], hasValidFormat: boolean } {
    const filePath = path.join(__dirTestData, fileName);
    if (!fs.existsSync(filePath)) {
        throw new Error("File Not Found: " + filePath);
    }    
    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const range = XLSX.utils.decode_range(sheet["!ref"]!);    
    // Get headers from first row
    const headers: string[] = [];
    for (let C = range.s.c; C <= range.e.c; C++) {
        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: C });
        const cell = sheet[cellAddress];
        if (cell && cell.v) {
            headers.push(cell.v.toString().trim());
        }
    } Logger(`Excel headers found: ${headers.join(', ')}`);    
    // Check if specific columns exist and validate format
    let hasValidFormat = true;
    const headerMap: Record<string, number> = {};
    headers.forEach((header, index) => {
        headerMap[header] = index;
    });    
    // Validate that Avail. Qty, Target Price, and Price columns have numeric/currency format
    if (headerMap["Avail. Qty"] !== undefined) {
        const col = headerMap["Avail. Qty"];
        for (let R = 1; R <= Math.min(range.e.r, 5); R++) {
            const cell = sheet[XLSX.utils.encode_cell({ r: R, c: col })];
            if (cell && cell.t !== 'n' && cell.t !== 's') {
                Logger(`Warning: Avail. Qty cell at row ${R} is not numeric (type: ${cell.t})`);
            }
        }
    } if (headerMap["Target Price"] !== undefined) {
        const col = headerMap["Target Price"];
        for (let R = 1; R <= Math.min(range.e.r, 5); R++) {
            const cell = sheet[XLSX.utils.encode_cell({ r: R, c: col })];
            if (cell && cell.t !== 'n') {
                Logger(`Warning: Target Price cell at row ${R} is not numeric (type: ${cell.t})`);
            }
        }
    } if (headerMap["Price"] !== undefined) {
        const col = headerMap["Price"];
        for (let R = 1; R <= Math.min(range.e.r, 5); R++) {
            const cell = sheet[XLSX.utils.encode_cell({ r: R, c: col })];
            if (cell && cell.t !== 'n') {
                Logger(`Warning: Price cell at row ${R} is not numeric (type: ${cell.t})`);
            }
        }
    } return { headers, hasValidFormat };
}
export function getExcelRowData(fileName: string, productID: string, grade: string): { availQty: string, targetPrice: string } | null {
    const filePath = path.join(__dirTestData, fileName);
    if (!fs.existsSync(filePath)) {
        throw new Error("File Not Found: " + filePath);
    }
    
    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const range = XLSX.utils.decode_range(sheet["!ref"]!);
    
    // Find header column indices
    const headerMap: Record<string, number> = {};
    for (let C = range.s.c; C <= range.e.c; C++) {
        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: C });
        const cell = sheet[cellAddress];
        if (cell && cell.v) {
            headerMap[cell.v.toString().trim()] = C;
        }
    }
    
    // Find the row matching productID and grade
    for (let R = 1; R <= range.e.r; R++) {
        const productCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["ProductId"] })];
        const gradeCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Grade"] })];
        
        if (!productCell || !gradeCell) continue;
        
        if (productCell.v == productID && gradeCell.v == grade) {
            const availQtyCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Avail. Qty"] })];
            const targetPriceCell = sheet[XLSX.utils.encode_cell({ r: R, c: headerMap["Target Price"] })];
            
            return {
                availQty: availQtyCell ? String(availQtyCell.v) : "",
                targetPrice: targetPriceCell ? String(targetPriceCell.v) : ""
            };
        }
    }
    
    Logger(`Row not found for ProductId: ${productID}, Grade: ${grade}`);
    return null;
}

export function getAllTargetPricesFromExcel(fileName: string): number[] {
    const filePath = path.join(__dirTestData, fileName);
    if (!fs.existsSync(filePath)) {
        throw new Error("File Not Found: " + filePath);
    }
    
    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const range = XLSX.utils.decode_range(sheet["!ref"]!);
    
    // Find header column indices
    const headerMap: Record<string, number> = {};
    for (let C = range.s.c; C <= range.e.c; C++) {
        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: C });
        const cell = sheet[cellAddress];
        if (cell && cell.v) {
            headerMap[cell.v.toString().trim()] = C;
        }
    }
    
    const targetPrices: number[] = [];
    const targetPriceCol = headerMap["Target Price"];
    
    if (targetPriceCol === undefined) {
        Logger("Target Price column not found in Excel");
        return targetPrices;
    }
    
    // Get all target prices from rows
    for (let R = 1; R <= range.e.r; R++) {
        const targetPriceCell = sheet[XLSX.utils.encode_cell({ r: R, c: targetPriceCol })];
        if (targetPriceCell && targetPriceCell.v) {
            const price = Number(targetPriceCell.v);
            if (!isNaN(price)) {
                targetPrices.push(price);
            }
        }
    }
    
    return targetPrices;
}

/**
 * Case Lot row data structure matching Case Lot Excel/grid columns
 */
export interface CaseLotRowData {
    sku: string;
    modelFamily: string;
    casePackQty: number;     // Units per case
    availableCases: number;  // Avl. Cases
    unitPrice: number;       // Unit Price
    casePrice: number;       // Case Price
}

/**
 * Read Case Lot Excel file and parse it into structured data.
 * Uses header-based detection so column order doesn't matter.
 * Expected headers: SKU, Model Family, Case Pack Qty, Avl. Cases, Unit Price, Case Price
 * @param filePath Path to the Excel file
 * @returns Array of Case Lot row data objects
 */
export function readCaseLotExcel(filePath: string): CaseLotRowData[] {
    if (!fs.existsSync(filePath)) {
        throw new Error(`Case Lot Excel file not found: ${filePath}`);
    }

    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];

    const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as any[][];

    if (rows.length < 2) {
        Logger("Case Lot Excel file has no data rows");
        return [];
    }

    // Header-based column detection
    const headers = rows[0].map((h: any) => String(h || '').trim().toLowerCase());
    Logger(`Case Lot Excel headers: ${JSON.stringify(rows[0])}`);

    const findCol = (keywords: string[]): number => {
        return headers.findIndex((h: string) => keywords.some(kw => h.includes(kw)));
    };

    const SKU_COL = findCol(['sku']);
    const MODEL_COL = findCol(['model']);
    const PACK_QTY_COL = findCol(['case pack', 'pack qty']);
    const AVL_CASES_COL = findCol(['avl', 'available']);
    const UNIT_PRICE_COL = findCol(['unit price']);
    const CASE_PRICE_COL = findCol(['case price']);

    Logger(`Column mapping: SKU=${SKU_COL}, Model=${MODEL_COL}, PackQty=${PACK_QTY_COL}, AvlCases=${AVL_CASES_COL}, UnitPrice=${UNIT_PRICE_COL}, CasePrice=${CASE_PRICE_COL}`);

    const parseNum = (val: any) => parseFloat(String(val || '0').replace(/[$,]/g, '')) || 0;
    const parseInt_ = (val: any) => {
        const s = String(val || '0').replace(/[,+]/g, '').trim();
        return s === '' ? 0 : parseInt(s) || 0;
    };

    const data: CaseLotRowData[] = [];

    for (let i = 1; i < rows.length; i++) {
        const row = rows[i];
        if (!row || row.length === 0) continue;

        const sku = SKU_COL >= 0 ? (row[SKU_COL] || '').toString().trim() : '';
        if (!sku) continue;

        data.push({
            sku,
            modelFamily: MODEL_COL >= 0 ? (row[MODEL_COL] || '').toString().trim() : '',
            casePackQty: PACK_QTY_COL >= 0 ? parseInt_(row[PACK_QTY_COL]) : 0,
            availableCases: AVL_CASES_COL >= 0 ? parseInt_(row[AVL_CASES_COL]) : 0,
            unitPrice: UNIT_PRICE_COL >= 0 ? parseNum(row[UNIT_PRICE_COL]) : 0,
            casePrice: CASE_PRICE_COL >= 0 ? parseNum(row[CASE_PRICE_COL]) : 0,
        });
    }

    Logger(`Parsed ${data.length} Case Lot rows from Excel file`);
    return data;
}

/**
 * RMA row data structure matching RMA Details Excel columns
 */
export interface RMARowData {
    imei: string;           // IMEI/Serial
    orderNumber: string;    // Order Number
    shipDate: string;       // Ship Date
    sku: string;            // SKU
    description: string;    // Description
    price: number;          // Original Price
    returnReason: string;   // Return Reason
}

/**
 * Read RMA Details Excel file and parse it into structured data
 * Excel headers: IMEI/Serial, Order Number, Ship Date, SKU, Description, Original Price, Return Reason
 * @param filePath Path to the Excel file
 * @returns Array of RMA row data objects
 */
export function readRMAExcel(filePath: string): RMARowData[] {
    if (!fs.existsSync(filePath)) {
        throw new Error(`RMA Excel file not found: ${filePath}`);
    }

    const workbook = XLSX.readFile(filePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];

    // Convert sheet to JSON with headers
    const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as any[][];

    if (rows.length < 2) {
        Logger("RMA Excel file has no data rows");
        return [];
    }

    Logger(`RMA Excel headers: ${JSON.stringify(rows[0])}`);

    // Find column indices from headers
    const headers = rows[0].map((h: any) => String(h || '').trim().toLowerCase());
    const getColIndex = (name: string): number => {
        return headers.findIndex((h: string) => h.includes(name));
    };

    const IMEI_COL = getColIndex('imei') >= 0 ? getColIndex('imei') : 0;
    const ORDER_COL = getColIndex('order') >= 0 ? getColIndex('order') : 1;
    const SHIP_COL = getColIndex('ship') >= 0 ? getColIndex('ship') : 2;
    const SKU_COL = getColIndex('sku') >= 0 ? getColIndex('sku') : 3;
    const DESC_COL = getColIndex('description') >= 0 ? getColIndex('description') : 4;
    const PRICE_COL = getColIndex('price') >= 0 ? getColIndex('price') : 5;
    const REASON_COL = getColIndex('reason') >= 0 ? getColIndex('reason') : 6;

    const data: RMARowData[] = [];

    // Parse data rows (skip header row)
    for (let i = 1; i < rows.length; i++) {
        const row = rows[i];
        if (!row || row.length === 0) continue;

        const imei = (row[IMEI_COL] || '').toString().trim();
        if (!imei) continue;

        const priceVal = row[PRICE_COL];
        const price = parseFloat(String(priceVal || '0').replace('$', '').replace(',', '')) || 0;

        data.push({
            imei,
            orderNumber: (row[ORDER_COL] || '').toString().trim(),
            shipDate: (row[SHIP_COL] || '').toString().trim(),
            sku: (row[SKU_COL] || '').toString().trim(),
            description: (row[DESC_COL] || '').toString().trim(),
            price,
            returnReason: (row[REASON_COL] || '').toString().trim()
        });
    }

    Logger(`Parsed ${data.length} RMA rows from Excel file`);
    return data;
}
```
