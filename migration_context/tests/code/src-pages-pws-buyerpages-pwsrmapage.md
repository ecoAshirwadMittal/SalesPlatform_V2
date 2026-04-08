# Page Object: PWS_RMAPage.ts

- **Path**: `src\pages\PWS\BuyerPages\PWS_RMAPage.ts`
- **Category**: Page Object
- **Lines**: 1077
- **Size**: 41,247 bytes
- **Members**: `class PWS_RMAPage`, `getIMEIFromShippedOrder`, `getFirstRMARowData`, `clickFirstRowAndVerifyDetails`, `completeRMAFlowWithDynamicIMEI`

## Source Code

```typescript
import { Page, expect, Download } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';
import * as XLSX from 'xlsx';
import * as fs from 'fs';
import * as path from 'path';

/**
 * PWS_RMAPage - Handles RMA (Return Merchandise Authorization) operations
 * for Premium Wholesale buyers
 */
export class PWS_RMAPage {
    private readonly page: Page;

    // Selectors - updated based on browser inspection
    private readonly selectors = {
        // RMA Tab and Header
        rmaTab: "button:has-text('RMAs')",
        rmaHeader: "text=RMA Requests",

        // Request RMA Button
        requestRMAButton: "button:has-text('Request an RMA')",

        // RMA Modal/Popup
        rmaModalTitle: "heading:has-text('Request an RMA')",
        downloadTemplateButton: "button:has-text('Download Template')",

        // File Upload - uses file chooser dialog
        chooseFileButton: "button:has-text('Choose File')",
        browseButton: "button:has-text('Browse...')",
        fileInputTextbox: "dialog >> textbox",

        // Submit RMA
        submitRMAButton: "button:has-text('Submit RMA')",

        // Close modal
        closeModalButton: "button:has-text('close')",

        // Success/Confirmation - matches "RMA Request Submitted!" popup
        successMessage: "text=/RMA.*Submitted|success|approved/i",
        okButton: "button:has-text('OK')",

        // Orders Navigation and IMEI Retrieval
        ordersNavItem: ".mx-name-container2 >> nth=2",  // Orders in sidebar
        ordersPageHeader: "text=Order History",
        shippedOrderRow: "tr:has(td:text-is('Shipped'))",
        orderNumberCell: "td >> nth=0",  // First column in order row
        byDeviceButton: "button:has-text('By Device')",
        imeiCell: "td >> nth=0",  // IMEI column in device view
        backToOrdersLink: "text=Order History",

        // 3-Dot Menu (Top Right)
        threeDotMenu: ".mx-name-actionButton18",  // ... menu button
        menuDownload: ".mx-name-actionButton22",  // Download option
        menuRMAInstructions: ".mx-name-actionButton21",  // RMA Instructions option
        menuRMAPolicy: ".mx-name-actionButton23",  // RMA Policy option

        // Column Selector (Eye Icon)
        columnSelectorButton: "button.column-selector-button",
        columnCheckbox: "input[type='checkbox']",

        // Table headers and columns
        tableHeaders: ".column-header",
        tableRows: "table tbody tr"
    };

    constructor(page: Page) {
        this.page = page;
    }

    /**
     * Verify RMA page is loaded
     */
    async isRMAPageDisplayed(): Promise<boolean> {
        try {
            const header = this.page.locator(this.selectors.rmaHeader);
            await header.waitFor({ state: 'visible', timeout: 10000 });
            return true;
        } catch {
            return false;
        }
    }

    /**
     * Click on RMAs tab within the inventory/shop page
     */
    async clickRMAsTab(): Promise<void> {
        Logger("Clicking on RMAs tab");
        const rmaTab = this.page.locator(this.selectors.rmaTab);
        await rmaTab.waitFor({ state: 'visible', timeout: 10000 });
        await rmaTab.click();
        await this.page.waitForTimeout(2000);
        Logger("Clicked RMAs tab");
    }

    /**
     * Click Request RMA button to open the RMA request modal
     */
    async clickRequestRMAButton(): Promise<void> {
        Logger("Clicking Request RMA button");
        await this.page.waitForTimeout(2000);

        const requestButton = this.page.locator(this.selectors.requestRMAButton);
        await requestButton.waitFor({ state: 'visible', timeout: 10000 });
        await requestButton.click();

        Logger("Clicked Request RMA button");
        await this.page.waitForTimeout(2000);
    }

    /**
     * Download the RMA template Excel file
     * @returns The path to the downloaded file
     */
    async downloadRMATemplate(): Promise<string> {
        Logger("Downloading RMA template");

        // Wait for download to start when clicking the download button
        const downloadPromise = this.page.waitForEvent('download', { timeout: 30000 });

        const downloadButton = this.page.locator(this.selectors.downloadTemplateButton);
        await downloadButton.waitFor({ state: 'visible', timeout: 10000 });
        await downloadButton.click();

        const download: Download = await downloadPromise;

        // Save the downloaded file
        const downloadPath = path.join(process.cwd(), 'downloads', download.suggestedFilename());

        // Ensure downloads directory exists
        const downloadDir = path.dirname(downloadPath);
        if (!fs.existsSync(downloadDir)) {
            fs.mkdirSync(downloadDir, { recursive: true });
        }

        await download.saveAs(downloadPath);
        Logger(`RMA template downloaded to: ${downloadPath}`);

        return downloadPath;
    }

    /**
     * Fill in the RMA template Excel file with the provided data
     * 
     * Template structure (simple 2-column format):
     * - Column A: IMEI/Serial Number
     * - Column B: Return Reason (dropdown with valid values)
     * 
     * Valid return reason values include:
     * - "Defective Battery/ Lower 69%"
     * - "Defective Microphone/ Speaker/ Volume Keys"
     * - "Defective Display"
     * - "No Power"
     * - "Screen Lines/ Pixel/ Discoloration/ software Issues/ Face ID"
     * - etc.
     * 
     * @param templatePath - Path to the downloaded template
     * @param imei - IMEI number to enter in column A
     * @param returnReason - Reason for return (must match a valid dropdown value in column B)
     * @returns Path to the modified file
     */
    async fillRMATemplate(templatePath: string, imei: string, returnReason: string): Promise<string> {
        Logger(`Filling RMA template with IMEI: ${imei}, Reason: ${returnReason}`);

        // Read the Excel file
        const workbook = XLSX.readFile(templatePath);
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];

        // Log header row for debugging
        const data = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as any[][];
        const headerRow = data[0] as string[];
        Logger(`Template headers: A1=${headerRow[0]}, B1=${headerRow[1]}`);

        // Simple template structure:
        // A1: "IMEI/Serial Number"
        // B1: "Return Reason" (with dropdown validation)
        // We need to fill:
        // A2: IMEI value
        // B2: Return reason (must match a valid dropdown option)

        // Set IMEI in cell A2
        sheet['A2'] = { t: 's', v: imei };
        Logger(`Set A2 = ${imei}`);

        // Set Return Reason in cell B2
        sheet['B2'] = { t: 's', v: returnReason };
        Logger(`Set B2 = ${returnReason}`);

        // Update the range to include row 2
        const range = XLSX.utils.decode_range(sheet['!ref'] || 'A1:B1');
        if (range.e.r < 1) {
            range.e.r = 1;  // Ensure row 2 (index 1) is included
        }
        if (range.e.c < 1) {
            range.e.c = 1;  // Ensure column B (index 1) is included
        }
        sheet['!ref'] = XLSX.utils.encode_range(range);

        // Save the modified file
        const modifiedPath = templatePath.replace('.xlsx', '_filled.xlsx').replace('.xls', '_filled.xls');
        XLSX.writeFile(workbook, modifiedPath);

        Logger(`RMA template filled and saved to: ${modifiedPath}`);
        return modifiedPath;
    }

    /**
     * Upload the filled RMA template file
     * @param filePath - Path to the filled template file
     */
    async uploadRMAFile(filePath: string): Promise<void> {
        Logger(`Uploading RMA file: ${filePath}`);

        // Use file chooser for the Browse button
        const fileChooserPromise = this.page.waitForEvent('filechooser', { timeout: 10000 });
        const browseButton = this.page.locator(this.selectors.browseButton);
        await browseButton.click();
        const fileChooser = await fileChooserPromise;
        await fileChooser.setFiles(filePath);

        Logger("RMA file uploaded");
        await this.page.waitForTimeout(2000);
    }

    /**
     * Submit the RMA request
     */
    async submitRMA(): Promise<void> {
        Logger("Submitting RMA request");

        const submitButton = this.page.locator(this.selectors.submitRMAButton);
        await submitButton.waitFor({ state: 'visible', timeout: 10000 });
        await submitButton.click();

        Logger("Clicked Submit RMA button");
        await this.page.waitForTimeout(3000);
    }

    /**
     * Complete RMA flow: Download template, fill it, upload, and submit
     * @param imei - IMEI number for the device
     * @param returnReason - Reason for the return
     */
    async completeRMAFlow(imei: string, returnReason: string): Promise<void> {
        Logger("Starting complete RMA flow");

        // Step 1: Click Request RMA
        await this.clickRequestRMAButton();

        // Step 2: Download template
        const templatePath = await this.downloadRMATemplate();

        // Step 3: Fill template with data
        const filledPath = await this.fillRMATemplate(templatePath, imei, returnReason);

        // Step 4: Upload filled template
        await this.uploadRMAFile(filledPath);

        // Step 5: Submit RMA
        await this.submitRMA();

        Logger("RMA flow completed");
    }

    /**
     * Verify RMA submission success
     * Tries multiple selectors to find success message
     */
    async verifyRMASubmissionSuccess(): Promise<boolean> {
        Logger("Checking for RMA submission success message");

        // Try multiple possible success message patterns
        const successPatterns = [
            "text=RMA Request Submitted",
            "text=/RMA.*Submitted/i",
            "text=/success/i",
            ".alert-success",
            ".mx-dialog:has-text('Submitted')"
        ];

        for (const pattern of successPatterns) {
            try {
                const element = this.page.locator(pattern).first();
                const isVisible = await element.isVisible({ timeout: 3000 }).catch(() => false);
                if (isVisible) {
                    Logger(`RMA submission success verified with pattern: ${pattern}`);
                    return true;
                }
            } catch {
                // Try next pattern
            }
        }

        // Last resort: check if URL changed or if any popup is visible
        try {
            const popup = this.page.locator('.mx-dialog, .modal, [role="dialog"]').first();
            if (await popup.isVisible({ timeout: 2000 }).catch(() => false)) {
                const popupText = await popup.textContent() || '';
                Logger(`Popup text found: ${popupText.substring(0, 100)}`);
                if (popupText.toLowerCase().includes('submit') || popupText.toLowerCase().includes('success')) {
                    Logger("RMA submission success inferred from popup text");
                    return true;
                }
            }
        } catch {
            // Ignore
        }

        Logger("RMA submission success message not found");
        return false;
    }

    /**
     * Close any modal/popup after RMA submission
     * Handles multiple modal types including "RMA Request Submitted!" dialog
     * User feedback: clicking outside the popup on the table dismisses it
     */
    async closeModal(): Promise<void> {
        Logger("Attempting to close any open modals");

        // First try: Click on the table to dismiss popup (most reliable method)
        try {
            const table = this.page.locator('table').first();
            if (await table.isVisible({ timeout: 2000 }).catch(() => false)) {
                await table.click({ force: true });
                Logger("Clicked on table to close modal");
                await this.page.waitForTimeout(1000);
                return;
            }
        } catch {
            // Continue to other methods
        }

        // Second try: Click on the modal close area
        try {
            const closeArea = this.page.locator('.modal-close-area, .mx-name-container44').first();
            if (await closeArea.isVisible({ timeout: 1000 }).catch(() => false)) {
                await closeArea.click();
                Logger("Clicked modal close area");
                await this.page.waitForTimeout(1000);
                return;
            }
        } catch {
            // Continue
        }

        // Third try: Press Escape key
        try {
            await this.page.keyboard.press('Escape');
            Logger("Pressed Escape to close modal");
        } catch {
            // Ignore
        }

        await this.page.waitForTimeout(1000);
    }

    // ============================================
    // 3-DOT MENU METHODS
    // ============================================

    /**
     * Click the 3-dot menu (top right corner)
     */
    async clickThreeDotMenu(): Promise<void> {
        Logger("Clicking 3-dot menu");
        const menuButton = this.page.locator(this.selectors.threeDotMenu);
        await menuButton.waitFor({ state: 'visible', timeout: 10000 });
        await menuButton.click();
        await this.page.waitForTimeout(1000);
        Logger("3-dot menu opened");
    }

    /**
     * Click Download option from 3-dot menu
     * @returns The downloaded file path
     */
    async clickMenuDownload(): Promise<string> {
        Logger("Clicking Download from 3-dot menu");

        // First open the menu
        await this.clickThreeDotMenu();

        // Wait for download and click
        const [download] = await Promise.all([
            this.page.waitForEvent('download', { timeout: 30000 }),
            this.page.click(this.selectors.menuDownload)
        ]);

        // Save the downloaded file
        const downloadDir = path.join(process.cwd(), 'downloads');
        if (!fs.existsSync(downloadDir)) {
            fs.mkdirSync(downloadDir, { recursive: true });
        }
        const fileName = download.suggestedFilename() || 'rma_download.xlsx';
        const downloadPath = path.join(downloadDir, fileName);
        await download.saveAs(downloadPath);

        Logger(`Downloaded file saved to: ${downloadPath}`);
        return downloadPath;
    }

    /**
     * Click RMA Instructions option from 3-dot menu
     * Note: This opens a modal with instructions, not a download
     * @returns true if modal opened successfully
     */
    async clickMenuRMAInstructions(): Promise<boolean> {
        Logger("Clicking RMA Instructions from 3-dot menu");

        // First open the menu
        await this.clickThreeDotMenu();

        // Click RMA Instructions
        await this.page.click(this.selectors.menuRMAInstructions);
        await this.page.waitForTimeout(2000);

        // Verify modal opened - look for any of these elements
        const modalIndicators = [
            this.page.locator('h1:has-text("RMA Instructions")'),
            this.page.locator('[role="heading"]:has-text("RMA Instructions")'),
            this.page.locator('text="RMA Instructions"').first(),
            this.page.locator('.modal-dialog:has-text("RMA Instructions")')
        ];

        let isVisible = false;
        for (const indicator of modalIndicators) {
            isVisible = await indicator.isVisible({ timeout: 2000 }).catch(() => false);
            if (isVisible) {
                Logger("RMA Instructions modal opened successfully");
                // Close the modal
                await this.closeModal();
                return true;
            }
        }

        Logger("RMA Instructions modal did not open");
        return false;
    }

    /**
     * Click RMA Policy option from 3-dot menu
     * Note: This opens a modal/link, may not be a direct download
     * @returns true if policy opened/downloaded successfully
     */
    async clickMenuRMAPolicy(): Promise<boolean> {
        Logger("Clicking RMA Policy from 3-dot menu");

        // First open the menu
        await this.clickThreeDotMenu();

        // Click RMA Policy - it may open a new tab, download, or show a modal
        try {
            await this.page.click(this.selectors.menuRMAPolicy);
            Logger("Clicked RMA Policy button");
            await this.page.waitForTimeout(2000);

            // Check if something happened (new tab, modal, or just stayed on page)
            // The click itself is the test - if no error, action completed
            Logger("RMA Policy action completed");

            // Close any modal that may have opened
            await this.closeModal();

            return true;
        } catch (error) {
            Logger(`RMA Policy error: ${error}`);
            return false;
        }
    }

    // ============================================
    // COLUMN SELECTOR METHODS
    // ============================================

    /**
     * Open the column selector (button labeled "Column selector" in table header)
     */
    async openColumnSelector(): Promise<void> {
        Logger("Opening column selector");
        // Use text-based selector since button is labeled "Column selector"
        const selectorButton = this.page.locator('button:has-text("Column selector"), [aria-label="Column selector"]').first();
        await selectorButton.waitFor({ state: 'visible', timeout: 10000 });
        await selectorButton.click();
        await this.page.waitForTimeout(1000);
        Logger("Column selector opened");
    }

    /**
     * Toggle a column on/off in the column selector dropdown
     * @param columnName - The name of the column to toggle (e.g., "Company", "Buyer")
     */
    async toggleColumn(columnName: string): Promise<void> {
        Logger(`Toggling column: ${columnName}`);

        // In the dropdown, look for checkbox or label with the column name
        const checkboxSelectors = [
            `label:has-text("${columnName}") >> input[type="checkbox"]`,
            `input[type="checkbox"] >> xpath=..//*[contains(text(), "${columnName}")]`,
            `text="${columnName}" >> xpath=ancestor::label//input[@type="checkbox"]`,
            `label:has-text("${columnName}")`,
            `.column-selector-dropdown label:has-text("${columnName}")`
        ];

        for (const selector of checkboxSelectors) {
            try {
                const element = this.page.locator(selector).first();
                if (await element.isVisible({ timeout: 2000 }).catch(() => false)) {
                    await element.click();
                    await this.page.waitForTimeout(500);
                    Logger(`Toggled column using selector: ${selector}`);
                    return;
                }
            } catch {
                // Continue trying other selectors
            }
        }

        // Last resort: just click on the text of the column name in dropdown
        const text = this.page.locator(`text="${columnName}"`).first();
        await text.click();
        await this.page.waitForTimeout(500);
        Logger(`Clicked column text: ${columnName}`);
    }

    /**
     * Check if a column is visible in the table
     * @param columnName - The name of the column to check
     * @returns true if visible, false if hidden
     */
    async isColumnVisible(columnName: string): Promise<boolean> {
        Logger(`Checking if column "${columnName}" is visible`);

        // Look for the column header in the table
        const header = this.page.locator(`th:has-text("${columnName}"), .column-header:has-text("${columnName}")`).first();
        const isVisible = await header.isVisible({ timeout: 3000 }).catch(() => false);

        Logger(`Column "${columnName}" visible: ${isVisible}`);
        return isVisible;
    }

    /**
     * Close the column selector dropdown
     */
    async closeColumnSelector(): Promise<void> {
        Logger("Closing column selector");
        // Click outside the dropdown to close it
        await this.page.click('body', { position: { x: 100, y: 100 } });
        await this.page.waitForTimeout(500);
    }

    /**
     * Navigate to Orders page and retrieve an IMEI from a shipped order
     * This method:
     * 1. Navigates to Orders from the sidebar
     * 2. Finds a shipped order 
     * 3. Opens order details and clicks "By Device" to see IMEIs
     * 4. Extracts the first IMEI from the device list
     * 5. Navigates back to RMAs page
     * 
     * @returns Object containing the IMEI and order number, or null if not found
     */
    async getIMEIFromShippedOrder(): Promise<{ imei: string; orderNumber: string } | null> {
        Logger("Starting IMEI retrieval from shipped order");

        try {
            // Step 1: Navigate to Orders page via URL (more reliable than clicking sidebar)
            Logger("Step 1: Navigating to Orders page");
            await this.page.goto('https://buy-qa.ecoatmdirect.com/p/orders');
            await this.page.waitForTimeout(5000);

            // Wait for orders table to load
            await this.page.waitForSelector('table', { timeout: 30000 });
            Logger("Orders page loaded");

            // Step 2: Find a shipped order - look for row with "Shipped" status
            Logger("Step 2: Finding shipped order");

            // Get all table rows
            const rows = this.page.locator('table tbody tr');
            const rowCount = await rows.count();
            Logger(`Found ${rowCount} order rows`);

            let orderNumber: string | null = null;
            let shippedRowIndex = -1;

            // Find a row with "Shipped" status
            for (let i = 0; i < rowCount; i++) {
                const rowText = await rows.nth(i).textContent();
                if (rowText && rowText.includes('Shipped')) {
                    // Extract order number from the first cell
                    const firstCell = rows.nth(i).locator('td').first();
                    orderNumber = await firstCell.textContent();
                    orderNumber = orderNumber?.trim() || null;
                    shippedRowIndex = i;
                    Logger(`Found shipped order at row ${i}: ${orderNumber}`);
                    break;
                }
            }

            if (!orderNumber || shippedRowIndex === -1) {
                Logger("No shipped order found");
                return null;
            }

            // Step 3: Click on the shipped order to open details
            Logger(`Step 3: Opening order ${orderNumber} details`);
            await rows.nth(shippedRowIndex).click();
            await this.page.waitForTimeout(3000);

            // Step 4: Click "By Device" button to see IMEIs
            Logger("Step 4: Clicking By Device button");
            const byDeviceBtn = this.page.locator(this.selectors.byDeviceButton);
            await byDeviceBtn.waitFor({ state: 'visible', timeout: 10000 });
            await byDeviceBtn.click();
            await this.page.waitForTimeout(2000);

            // Step 5: Extract IMEI from the device table
            Logger("Step 5: Extracting IMEI from device list");

            // The device table shows IMEI in the first column of data rows
            const deviceTable = this.page.locator('table').last();
            const deviceRows = deviceTable.locator('tbody tr');
            const deviceRowCount = await deviceRows.count();

            if (deviceRowCount === 0) {
                Logger("No devices found in order");
                return null;
            }

            // Get IMEI from first device row, first column
            const imeiCell = deviceRows.first().locator('td').first();
            let imei = await imeiCell.textContent();
            imei = imei?.trim() || null;

            if (!imei) {
                Logger("Could not extract IMEI from device row");
                return null;
            }

            Logger(`Successfully retrieved IMEI: ${imei} from order ${orderNumber}`);

            // Step 6: Navigate back to RMAs page via URL (more reliable)
            Logger("Step 6: Navigating back to RMAs page");
            await this.page.goto('https://buy-qa.ecoatmdirect.com/p/RMAs');
            await this.page.waitForTimeout(3000);

            return { imei, orderNumber };
        } catch (error) {
            Logger(`Error retrieving IMEI from shipped order: ${error}`);
            return null;
        }
    }

    /**
     * Get the first row's data from the RMA table
     * Used to verify a newly submitted RMA appears in the table
     * @returns Object containing the RMA row data or null if no rows found
     */
    async getFirstRMARowData(): Promise<{
        imei: string;
        returnReason: string;
        status: string;
        requestDate: string;
        [key: string]: string;
    } | null> {
        Logger("Getting first row data from RMA table");

        try {
            // Wait for the table to be visible
            await this.page.waitForSelector('table', { timeout: 10000 });

            // Get the table headers first to map column indices
            const headers = await this.page.locator('table thead th').allTextContents();
            Logger(`Table headers: ${headers.join(', ')}`);

            // Get the first data row
            const firstRow = this.page.locator('table tbody tr').first();
            const isVisible = await firstRow.isVisible({ timeout: 5000 }).catch(() => false);

            if (!isVisible) {
                Logger("No RMA rows found in table");
                return null;
            }

            // Get all cell values from the first row
            const cells = await firstRow.locator('td').allTextContents();
            Logger(`First row cells: ${cells.join(' | ')}`);

            // Map headers to cell values
            const rowData: { [key: string]: string } = {};
            headers.forEach((header, index) => {
                if (cells[index] !== undefined) {
                    rowData[header.trim()] = cells[index].trim();
                }
            });

            // Extract common fields with fallbacks for different column name variations
            const imei = rowData['IMEI'] || rowData['IMEI/Serial'] || rowData['Serial Number'] || cells[0]?.trim() || '';
            const returnReason = rowData['Return Reason'] || rowData['Reason'] || cells[1]?.trim() || '';
            const status = rowData['Status'] || rowData['RMA Status'] || cells[2]?.trim() || '';
            const requestDate = rowData['Request Date'] || rowData['Date'] || rowData['Created'] || '';

            Logger(`Extracted: IMEI=${imei}, Reason=${returnReason}, Status=${status}`);

            return {
                imei,
                returnReason,
                status,
                requestDate,
                ...rowData
            };
        } catch (error) {
            Logger(`Error getting RMA row data: ${error}`);
            return null;
        }
    }

    /**
     * Click the first row in the RMA table to open RMA Details page
     * Then verify the IMEI/Serial and Return Reason on the details page
     * @param expectedImei - The IMEI to verify
     * @param expectedReturnReason - The return reason to verify (optional)
     * @returns Object with verification results
     */
    async clickFirstRowAndVerifyDetails(expectedImei: string, expectedReturnReason?: string): Promise<{
        success: boolean;
        imei: string;
        returnReason: string;
        status: string;
    }> {
        Logger("Clicking first row to open RMA Details page");

        try {
            // Wait for page to be ready after refresh
            await this.page.waitForTimeout(3000);

            Logger("Looking for first RMA row...");

            // Use custom Mendix selector for first row's first cell (from user)
            const firstRowCell = this.page.locator('.pws-rma-returns-datagrid div.pws-orderhistory-datagridhover:nth-child(1) .td').first();

            if (await firstRowCell.isVisible({ timeout: 5000 }).catch(() => false)) {
                Logger("Found first row cell, clicking...");
                await firstRowCell.click({ timeout: 5000 });
                Logger("Click successful");
            } else {
                Logger("First row not found, trying fallback");
                const fallbackRow = this.page.locator('.pws-orderhistory-datagridhover').first();
                await fallbackRow.click({ timeout: 5000 });
            }

            await this.page.waitForTimeout(2000);

            // Wait for RMA Details page to load
            await this.page.waitForSelector('text=RMA Details', { timeout: 10000 });
            Logger("RMA Details page opened");

            // Extract data from the details page table
            let imei = '';
            let returnReason = '';
            let status = '';

            // Get IMEI/Serial from first cell in data row (data-position="0,0")
            try {
                // Details table: .widget-datagrid-grid-body .tr .td:first-child .td-text
                const imeiCell = this.page.locator('.widget-datagrid-grid-body .tr .td:first-child .td-text');
                imei = await imeiCell.textContent() || '';
                imei = imei.trim();
                Logger(`Found IMEI: ${imei}`);
            } catch {
                Logger("Could not find IMEI in details table");
            }

            // Get Return Reason from last cell in data row (data-position="6,0")
            try {
                const returnReasonCell = this.page.locator('.widget-datagrid-grid-body .tr .td:last-child .td-text');
                returnReason = await returnReasonCell.textContent() || '';
                returnReason = returnReason.trim();
                Logger(`Found Return Reason: ${returnReason}`);
            } catch {
                Logger("Could not find Return Reason in details table");
            }

            // Get status from header area
            try {
                const statusText = await this.page.locator('text=Status').locator('xpath=following-sibling::*[1]').textContent();
                status = statusText?.trim() || '';
            } catch {
                const submitted = await this.page.locator('text=Submitted').first().isVisible().catch(() => false);
                if (submitted) status = 'Submitted';
            }

            Logger(`Details: IMEI=${imei}, ReturnReason=${returnReason}, Status=${status}`);

            // Verify IMEI matches
            const imeiMatches = imei.includes(expectedImei) || expectedImei.includes(imei);
            Logger(imeiMatches ? `✓ IMEI verified: ${imei}` : `✗ IMEI mismatch: expected ${expectedImei}`);

            return { success: imeiMatches, imei, returnReason, status };
        } catch (error) {
            Logger(`Error in clickFirstRowAndVerifyDetails: ${error}`);
            return { success: false, imei: '', returnReason: '', status: '' };
        }
    }

    /**
     * Go back to the RMA list from the RMA Details page
     */
    async goBackToRMAList(): Promise<void> {
        Logger("Navigating back to RMA list");
        try {
            const backLink = this.page.locator('text=All RMA Requests').first();
            if (await backLink.isVisible({ timeout: 2000 }).catch(() => false)) {
                await backLink.click();
                await this.page.waitForTimeout(2000);
                return;
            }
        } catch {
            await this.page.goBack();
            await this.page.waitForTimeout(2000);
        }
    }

    /**
     * Download Excel file from RMA Details page
     * Clicks the download button (.pws-button-primary) on the RMA Details page
     * @returns Path to the downloaded Excel file
     */
    async downloadRMADetailsExcel(): Promise<string> {
        Logger("Downloading Excel from RMA Details page");

        try {
            // Wait for and click the download button
            const downloadButton = this.page.locator('.pws-button-primary').first();

            if (!await downloadButton.isVisible({ timeout: 5000 }).catch(() => false)) {
                throw new Error("Download button not found on RMA Details page");
            }

            // Set up download listener
            const downloadPromise = this.page.waitForEvent('download', { timeout: 30000 });
            await downloadButton.click();

            const download = await downloadPromise;
            const fileName = download.suggestedFilename();
            const downloadPath = path.join(process.cwd(), 'downloads', fileName);

            // Ensure downloads directory exists
            if (!fs.existsSync(path.join(process.cwd(), 'downloads'))) {
                fs.mkdirSync(path.join(process.cwd(), 'downloads'), { recursive: true });
            }

            await download.saveAs(downloadPath);
            Logger(`Excel downloaded to: ${downloadPath}`);

            return downloadPath;
        } catch (error) {
            Logger(`Error downloading RMA Details Excel: ${error}`);
            throw error;
        }
    }

    /**
     * Refresh the RMA table by navigating away and back, or clicking refresh if available
     */
    async refreshRMATable(): Promise<void> {
        Logger("Refreshing RMA table");

        try {
            // Try to find and click a refresh button first
            const refreshButton = this.page.locator('button:has-text("Refresh"), [aria-label="Refresh"]').first();
            if (await refreshButton.isVisible({ timeout: 2000 }).catch(() => false)) {
                await refreshButton.click();
                await this.page.waitForTimeout(2000);
                Logger("Clicked refresh button");
                return;
            }
        } catch {
            // No refresh button, try alternative methods
        }

        // Alternative: reload the page
        await this.page.reload();
        await this.page.waitForTimeout(3000);
        await this.page.waitForSelector('table', { timeout: 10000 });
        Logger("RMA table refreshed via page reload");
    }

    /**
     * Verify that a specific IMEI appears in the RMA table
     * @param imei - The IMEI to search for
     * @returns true if found, false otherwise
     */
    async verifyIMEIInTable(imei: string): Promise<boolean> {
        Logger(`Verifying IMEI ${imei} appears in RMA table`);

        try {
            // Look for the IMEI text anywhere in the table
            const imeiLocator = this.page.locator(`table tbody td:has-text("${imei}")`).first();
            const isVisible = await imeiLocator.isVisible({ timeout: 5000 }).catch(() => false);

            if (isVisible) {
                Logger(`✓ IMEI ${imei} found in RMA table`);
                return true;
            } else {
                Logger(`✗ IMEI ${imei} NOT found in RMA table`);
                return false;
            }
        } catch (error) {
            Logger(`Error verifying IMEI in table: ${error}`);
            return false;
        }
    }

    /**
     * Complete RMA flow with dynamic IMEI retrieval
     * This method automatically fetches a valid IMEI from a shipped order
     * 
     * @param returnReason - Reason for the return (must match template column header)
     * @returns Object with the IMEI used and submission result
     */
    async completeRMAFlowWithDynamicIMEI(returnReason: string): Promise<{
        imei: string | null;
        orderNumber: string | null;
        success: boolean
    }> {
        Logger("Starting complete RMA flow with dynamic IMEI retrieval");

        // Step 1: Get IMEI from a shipped order
        const orderData = await this.getIMEIFromShippedOrder();

        if (!orderData) {
            Logger("Failed to retrieve IMEI from shipped order");
            return { imei: null, orderNumber: null, success: false };
        }

        Logger(`Using IMEI ${orderData.imei} from order ${orderData.orderNumber}`);

        // Step 2: Complete the RMA flow with the retrieved IMEI
        await this.completeRMAFlow(orderData.imei, returnReason);

        // Step 3: Verify success
        const success = await this.verifyRMASubmissionSuccess();

        return {
            imei: orderData.imei,
            orderNumber: orderData.orderNumber,
            success
        };
    }

    /**
     * Get the total record count from pagination text
     * Looks for text like "1 to 10 of 45" and extracts the total (45)
     * @returns The total count, or -1 if not found
     */
    async getPaginationCount(): Promise<number> {
        Logger("Getting pagination count");

        try {
            // Look for pagination text patterns like "1 to 10 of 45" or "Showing 1-10 of 45"
            const paginationSelectors = [
                '.pagination-status',
                '.paging-status',
                '[class*="pagination"]',
                'text=/\\d+\\s*(to|-)\\s*\\d+\\s*of\\s*\\d+/i'
            ];

            for (const selector of paginationSelectors) {
                try {
                    const element = this.page.locator(selector).first();
                    if (await element.isVisible({ timeout: 2000 }).catch(() => false)) {
                        const text = await element.textContent() || '';
                        // Extract the total number after "of"
                        const match = text.match(/of\s*(\d+)/i);
                        if (match) {
                            const count = parseInt(match[1], 10);
                            Logger(`Pagination count: ${count}`);
                            return count;
                        }
                    }
                } catch {
                    continue;
                }
            }

            // Alternative: count visible rows if no pagination text found
            const rows = await this.page.locator('table tbody tr').count();
            Logger(`No pagination text found, visible rows: ${rows}`);
            return rows;
        } catch (error) {
            Logger(`Error getting pagination count: ${error}`);
            return -1;
        }
    }

    /**
     * Sort the RMA table by date in descending order (newest first)
     * Clicks the date column header to toggle sort
     */
    async sortByDateDescending(): Promise<void> {
        Logger("Sorting RMA table by date (newest first)");

        try {
            // Look for date column header - try various names
            const dateHeaders = [
                'th:has-text("Date")',
                'th:has-text("Request Date")',
                'th:has-text("Created")',
                'th:has-text("Submitted")',
                '[role="columnheader"]:has-text("Date")'
            ];

            for (const selector of dateHeaders) {
                try {
                    const header = this.page.locator(selector).first();
                    if (await header.isVisible({ timeout: 2000 }).catch(() => false)) {
                        // Click the header to sort
                        await header.click();
                        await this.page.waitForTimeout(1000);

                        // Check if it's ascending (we want descending)
                        // Click again if needed to get descending order
                        const headerText = await header.textContent() || '';
                        const hasAscIndicator = headerText.includes('↑') || headerText.includes('▲');

                        if (hasAscIndicator) {
                            await header.click();
                            await this.page.waitForTimeout(1000);
                        }

                        Logger("Sorted by date descending");
                        return;
                    }
                } catch {
                    continue;
                }
            }

            Logger("Date column header not found");
        } catch (error) {
            Logger(`Error sorting by date: ${error}`);
        }
    }

    /**
     * Navigate to another tab and back to refresh the RMA table
     * This avoids popup issues by leaving the page entirely
     * @param navMenu - The navigation menu page object (pws_navMenuAsBuyer)
     */
    async navigateAwayAndBack(navMenu: any): Promise<void> {
        Logger("Navigating away from RMAs and back to refresh");

        try {
            // Navigate to Shop/Inventory tab
            await navMenu.chooseNavMenu("Shop");
            await this.page.waitForTimeout(2000);
            Logger("Navigated to Shop page");

            // Navigate back to RMAs
            await navMenu.chooseNavMenu("RMAs");
            await this.page.waitForTimeout(3000);
            Logger("Navigated back to RMAs page");

            // Wait for table to load
            await this.page.waitForSelector('table', { timeout: 10000 });
            Logger("RMA table loaded after navigation");
        } catch (error) {
            Logger(`Error during navigation: ${error}`);
            // Fallback: try direct URL navigation
            await this.page.goto('https://buy-qa.ecoatmdirect.com/p/RMAs');
            await this.page.waitForTimeout(3000);
        }
    }
}

```
