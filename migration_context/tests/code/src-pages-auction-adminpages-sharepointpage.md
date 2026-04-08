# Page Object: SharePointPage.ts

- **Path**: `src\pages\Auction\AdminPages\SharePointPage.ts`
- **Category**: Page Object
- **Lines**: 141
- **Size**: 7,252 bytes
- **Members**: `class SharePointPage`, `navigateToSharePointStageFolderCurrentWeek`, `validateSubmittedBids`, `navigateToSharePointSite`, `loginToSharePoint`, `gotoCurrentStageFolder`

## Source Code

```typescript
import { Page, expect } from "@playwright/test";
import user_data from '../../../utils/resources/user_data.json';
import { downloadFile, excelToCSV } from "../../../utils/helpers/data_utils";
import { AuctionItem } from "../../../utils/helpers/auc_item";
import fs from 'fs';
import path from "path";
import csvParser from "csv-parser";

export class SharePointPage {   
    private readonly usernameField = "//input[@type='email']";
    private readonly passwordField = "//input[@type='password']";
    private readonly confirmSignInModal = "//div[@id='lightbox']";        
    private readonly submitButton = "//input[@type='submit']"; 
    
    private readonly modifyDateButton = "//div[@role='columnheader']//span[@aria-label='Modified']";
    private readonly newerToOderSorting = "//button[@name='Newer to older']";
    private readonly secondDateFolderOnList = "//div[@class='ms-List-cell' and @data-list-index='1']//span/button";
    private readonly stageFolder = "//div[@class='ms-List-cell' and @data-list-index='0']//span/button"

    private readonly firstRowOnList = "//div[@class='ms-List-cell' and @data-list-index='0']";
    private readonly firstFileName = this.firstRowOnList + "//span/button";
    private readonly firstRowThreeDotMenu = this.firstRowOnList + "//button[@data-automationid='FieldRender-DotDotDot']";
    private readonly secondRowOnList = "//div[@class='ms-List-cell' and @data-list-index='1']";
    private readonly secondFileName = this.secondRowOnList + "//span/button"; 
    private readonly secondRowThreeDotMenu = this.secondRowOnList + "//button[@data-automationid='FieldRender-DotDotDot']";
    private readonly downloadDropDown = "//li[@role='presentation']//button[@name='Download']";

    constructor(private page: Page) {        
    }

    async navigateToSharePointStageFolderCurrentWeek() {
        await this.navigateToSharePointSite();
        await this.loginToSharePoint();
        await this.gotoCurrentStageFolder();
    }

    async downloadSubmittedBidFile(buyerCode: string): Promise<string> {
        const maxTimeBidsTransferToSharePoint = 300000;  // 3 mins
        const startTime = Date.now();
        let fileNameFirstRow: string;
        let fileNameSecondRow: string;
        let fileNameCSV: string;
        while (true) {
            fileNameFirstRow = await this.page.locator(this.firstFileName).textContent()??"";
            fileNameSecondRow = await this.page.locator(this.secondFileName).textContent()??"";

            if (fileNameFirstRow.includes(buyerCode)) {
                fileNameCSV = await this.downloadSubmittedBidFile_helper(fileNameFirstRow, this.firstRowOnList, this.firstRowThreeDotMenu);
                break;
            }
            if (fileNameSecondRow.includes(buyerCode)) {               
                fileNameCSV = await this.downloadSubmittedBidFile_helper(fileNameSecondRow, this.secondRowOnList, this.secondRowThreeDotMenu);
                break;
            }
            if(Date.now() - startTime > maxTimeBidsTransferToSharePoint) {
                throw new Error("Timed Out! Submitted-Bid File Tooks More Than 3 Minutes to Transfer to SharePoint")
            }
            await this.page.waitForTimeout(15000);
            await this.page.reload();
        } 
        return fileNameCSV;
    }

    private async downloadSubmittedBidFile_helper(fileName: string, firstRowLocator: string, threedotMenuLocator: string): Promise<string>{
        await this.page.locator(firstRowLocator).click();
        const fileNameCSV = fileName.substring(0,fileName.indexOf('.')).concat('.CSV');       
        await this.page.locator(firstRowLocator).click();
        await this.page.locator(threedotMenuLocator).click();
        await this.page.locator(this.downloadDropDown).waitFor({state:'visible'});
        await downloadFile(this.page, this.downloadDropDown, fileName);
        excelToCSV(fileName,fileNameCSV);
        await this.page.waitForTimeout(3000);
        return fileNameCSV;
    }

    async filterCSVRowByIDs(fileName:string, targetIDs: Set<string>): Promise<any[]> {
        const filePath = path.join("./src/test-data/",fileName)
        return new Promise((resolve, reject) => {
            const filteredRows: any[] = [];            
            fs.createReadStream(filePath).pipe(csvParser())
            .on('data', (row) => {
                const rowID = row["ecoATM Code"]?.trim();
                if (targetIDs.has(rowID)) {
                    filteredRows.push(row);
                }
            })
            .on('end', () => resolve(filteredRows))
            .on('error', reject);
        });
    }

    async validateSubmittedBids(submittedBids: Array<AuctionItem>, sharePointFilteredRows: any[]) {
        console.table(submittedBids);
        await this.page.waitForTimeout(5000);
        for (const eachBid of submittedBids) {
            const row = sharePointFilteredRows.find(r => r['ecoATM Code'] === eachBid.getProductID());
            const grade = eachBid.getGrade();
            if(!row) {
                console.error(`No ecoID ${eachBid.getProductID()} Not Found in this SharePoint File`);
            }            
            const sharePoint_bidPrice = row[grade];
            const maxPriceNoDecimal = row[`MAX of Grade ${grade}`];
            const sharePoint_maxPrice = `$${parseFloat(maxPriceNoDecimal.replace(/\$/g,'')).toFixed(2)}`;
            const sharePoint_qtyCap = row[`${grade} Quantity Cap`];
            const submitted_bidPrice = eachBid.getBidPriceString();
            const targetPrice = eachBid.getTargetPrice().replace(',','');
            const submitted_bidQty = eachBid.getQtyCap();
            expect(submitted_bidPrice).toEqual(sharePoint_bidPrice);
            expect(targetPrice).toEqual(sharePoint_maxPrice);
            expect(submitted_bidQty).toEqual(sharePoint_qtyCap);
        }     
    }    

    private async navigateToSharePointSite() {
        await this.page.waitForTimeout(3000);
        await this.page.goto(user_data.sharePointQATestingFolderURL);
    }

    private async loginToSharePoint() {
        await this.page.locator(this.usernameField).waitFor({state: 'visible'});
        await this.page.locator(this.usernameField).fill(user_data.SharePoint.username);
        await this.page.locator(this.submitButton).click();
        await this.page.locator(this.passwordField).waitFor({state: 'visible'});
        await this.page.locator(this.passwordField).fill(user_data.SharePoint.password);
        await this.page.locator(this.submitButton).click();
        await this.page.locator(this.confirmSignInModal).waitFor({state: 'visible'});
        await this.page.locator(this.submitButton).click();
    }    

    private async gotoCurrentStageFolder() {
        await this.page.locator(this.modifyDateButton).waitFor({state: 'visible'});
        await this.page.locator(this.modifyDateButton).click();
        await this.page.locator(this.newerToOderSorting).click();
        await this.page.waitForTimeout(2000);
        await this.page.locator(this.secondDateFolderOnList).click();
        await this.page.waitForTimeout(2000);
        await this.page.locator(this.stageFolder).click();
        await this.page.waitForTimeout(3000);    
    }  
}

```
