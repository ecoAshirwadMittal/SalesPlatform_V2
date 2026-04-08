# Page Object: ACC_AuctionSchedulePage.ts

- **Path**: `src\pages\Auction\AdminPages\ACC_AuctionSchedulePage.ts`
- **Category**: Page Object
- **Lines**: 290
- **Size**: 16,134 bytes
- **Members**: `class ACC_AuctionSchedulePage`, `createAuctionAndStartRoundOne`, `formatDate`, `formatTime`

## Source Code

```typescript
import { Locator, Page } from '@playwright/test'
import { NavMenuPage } from '../../CommonPages/NavMenuPage';
import { navTabs } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';


export class ACC_AuctionSchedulePage {
    //----------Inventory Page ----------
    private readonly Inventory_AuctionWeekDropdown = "//div[@class='widget-combobox-selected-items']";
    private readonly Inventory_AuctionWeekOptions = "//ul[contains(@class,'widget-combobox-menu-list') and @role='listbox']";
    private readonly Inventory_CreateAuctionButton = "//button[contains(@data-button-id,'Button9')]";
    private readonly Inventory_CreateAuctionButtonOnModal = "//button[contains(@data-button-id,'Create_Auction.actionButton1')]";

    //---------Auction Scheduling Page -------
    private readonly Scheduling_RoundOneFromDateInput = "(//div[@class='mx-compound-control']//input)[1]";
    private readonly Scheduling_RoundOneFromTimeInput = "(//div[@class='mx-compound-control']//input)[2]";
    private readonly Scheduling_RoundOneToDateInput = "(//div[@class='mx-compound-control']//input)[3]";
    private readonly Scheduling_RoundOneToTimeInput = "(//div[@class='mx-compound-control']//input)[4]";
    private readonly Scheduling_RoundTwoToDateInput = "(//div[@class='mx-compound-control']//input)[5]";
    private readonly Scheduling_RoundTwoToTimeInput = "(//div[@class='mx-compound-control']//input)[6]";
    private readonly Scheduling_RoundThreeToDateInput = "(//div[@class='mx-compound-control']//input)[7]";
    private readonly Scheduling_RoundThreeToTimeInput = "(//div[@class='mx-compound-control']//input)[8]";
    private readonly Scheduling_ScheduleButton = "//button[contains(@data-button-id,'Auction_Overview.actionButton5')]";
    private readonly Scheduling_ScheduleButtonOnModal = "//button[text()='Looks good, Schedule!']";

    //------- Scheduling Auction Page (under ACC)  -----------
    private readonly schedule_roundOneStatus = '//div[@data-position="5,2"]';
    private readonly schedule_roundTwoStatus = '//div[@data-position="5,1"]';
    private readonly schedule_roundThreeStatus = '//div[@data-position="5,0"]';
    private readonly schedule_roundOneOpenModal = "//div[contains(@class,'grid-body')]/div[3]//a";
    private readonly schedule_roundTwoOpenModal = "//div[contains(@class,'grid-body')]/div[2]//a";
    private readonly schedule_roundThreeOpenModal = "//div[contains(@class,'grid-body')]/div[1]//a";
    private readonly schedule_StartTimeOnModal = "(//div[@class='mx-compound-control']//input)[1]";
    private readonly schedule_EndTimeOnModal = "(//div[@class='mx-compound-control']//input)[2]";
    private readonly schedule_SaveButton = "//button[text()='Save']";

    //-------- Auctions Page (under ACC) --------------
    private readonly Auction_weekSortingIcon = "//span[text()='Auction title']/ancestor::div[contains(@class,'column-header')]";
    private readonly Auction_deleteIconFirstRow = "(//span[@class='mx-icon-lined mx-icon-trash-can'])[1]";
    private readonly Auction_proceedDeleteButton = "//button[text()='Proceed']";
    private readonly Auction_purgeCompletedButton = "//button[text()='OK']";
    

    constructor(private page: Page) { }

    //------------- Inventory and Auction Scheduling Pages --------------

    async createAuctionAndStartRoundOne() {
        const adminMainPage = new NavMenuPage(this.page);
        await adminMainPage.chooseMainNav(navTabs.Inventory);
        // Click the Auction Week dropdown to open it
        const dropdown = this.page.locator(this.Inventory_AuctionWeekDropdown);
        await dropdown.waitFor({ state: 'visible', timeout: 5000 });
        await dropdown.click();
        // Wait for the dropdown menu to be visible
        const dropdownMenu = this.page.locator(this.Inventory_AuctionWeekOptions);
        await dropdownMenu.waitFor({ state: 'visible', timeout: 5000 });
        // Click the second week option from the top
        const secondWeekOption = dropdownMenu.locator("li").nth(1);
        await secondWeekOption.waitFor({ state: 'visible', timeout: 5000 });
        await secondWeekOption.click();
        await this.page.waitForTimeout(2000);
        // Click the Create-Auction button once inventory for the selected week is loaded
        const createAuctionButton = this.page.locator(this.Inventory_CreateAuctionButton).first();
        await createAuctionButton.waitFor({ state: 'visible', timeout: 5000 });
        await createAuctionButton.click();
        const createAuctionModalButton = this.page.locator(this.Inventory_CreateAuctionButtonOnModal).first();
        await createAuctionModalButton.waitFor({ state: 'visible', timeout: 5000 });
        await createAuctionModalButton.click();
        await this.page.waitForTimeout(2000);
        // Entering Auction Date-Time for Round 1,2,3
        await this.inputAuctionDateTimeFields();
        // Click the Schedule button and confirm on the modal
        await this.page.locator(this.Scheduling_ScheduleButton).click();
        await this.page.waitForTimeout(2000);
        await this.page.locator(this.Scheduling_ScheduleButtonOnModal).click();
        await this.page.waitForTimeout(5000);
        Logger("Auction created and scheduled successfully.");
        // Wait for Round 1 to start
        await this.waitForRoundStatus(this.schedule_roundOneStatus, "Started");
    }

    //------------- Scheduled Auctions Page (under ACC) --------------

    async endAuctionRoundBySchedule(round: 1 | 2): Promise<void> {
        // Go to Scheduled Auctions tab
        const navigationBarPages = new NavMenuPage(this.page);
        await navigationBarPages.clickAuctionControlTab("Scheduled Auctions");
        await this.page.waitForTimeout(2000);
        // Open the correct round modal
        let roundModalButton: Locator;
        let endTimeStatusLocator: string;
        if (round === 1) {
            roundModalButton = this.page.locator(this.schedule_roundOneOpenModal);
            endTimeStatusLocator = this.schedule_roundOneStatus;
        } else if (round === 2) {
            roundModalButton = this.page.locator(this.schedule_roundTwoOpenModal);
            endTimeStatusLocator = this.schedule_roundTwoStatus;
        } else {
            throw new Error("Only round 1 or 2 are supported.");
        }
        await roundModalButton.waitFor({ state: 'visible', timeout: 5000 });
        await roundModalButton.click();
        // Enter End Time in modal (mm/dd/yyyy, hh:mm PM)
        const now = new Date();
        const dateStr = this.formatDate(now);
        const endTime = new Date(now.getTime() + 2 * 60000);
        const endTimeStr = this.formatTime(endTime);
        // Fill End Time field using helper
        await this.fillDateTimeInput(this.schedule_EndTimeOnModal, `${dateStr}, ${endTimeStr}`);
        // Click Save
        const saveButton = this.page.locator(this.schedule_SaveButton);
        await saveButton.waitFor({ state: 'visible', timeout: 5000 });
        await saveButton.click();
        await this.page.waitForTimeout(2000);
        // Wait for round to be closed
        await this.waitForRoundStatus(endTimeStatusLocator, "Closed");
    }

    async startAuctionRoundBySchedule(round: 2 | 3): Promise<void> {
        // Go to Scheduled Auctions tab
        const navigationBarPages = new NavMenuPage(this.page);
        await navigationBarPages.clickAuctionControlTab("Scheduled Auctions");
        await this.page.waitForTimeout(2000);
        // Open the correct round modal
        let roundModalButton: Locator;
        let roundStatusLocator: string;
        if (round === 2) {
            roundModalButton = this.page.locator(this.schedule_roundTwoOpenModal);
            roundStatusLocator = this.schedule_roundTwoStatus;
        } else if (round === 3) {
            roundModalButton = this.page.locator(this.schedule_roundThreeOpenModal);
            roundStatusLocator = this.schedule_roundThreeStatus;
        } else {
            throw new Error("Only round 2 or 3 are supported.");
        }
        await roundModalButton.waitFor({ state: 'visible', timeout: 5000 });
        await roundModalButton.click();
        // Enter Start Time in modal (mm/dd/yyyy, hh:mm PM)
        const now = new Date();
        const dateStr = this.formatDate(now);
        const startTime = new Date(now.getTime() + 1 * 60000);
        const startTimeStr = this.formatTime(startTime);
        // Fill Start Time field using helper
        await this.fillDateTimeInput(this.schedule_StartTimeOnModal, `${dateStr}, ${startTimeStr}`);
        // Click Save
        const saveButton = this.page.locator(this.schedule_SaveButton);
        await saveButton.waitFor({ state: 'visible', timeout: 5000 });
        await saveButton.click();
        await this.page.waitForTimeout(2000);
        // Wait for round status to be "Started"
        await this.waitForRoundStatus(roundStatusLocator, "Started");
    }

    //------------- Auctions Page (under ACC) --------------

    async deleteRecentAuctionWeek(): Promise<void> {
        // Navigate to Auctions page
        const navigationBarPages = new NavMenuPage(this.page);
        await navigationBarPages.clickAuctionControlTab("Auctions");
        await this.page.waitForTimeout(2000);
        // sort by week (descending)
        const auctionTitleHeader = this.page.locator(this.Auction_weekSortingIcon);
        await auctionTitleHeader.waitFor({ state: 'visible', timeout: 5000 });
        await auctionTitleHeader.click();
        await this.page.waitForTimeout(2000);
        // Click trash icon on the top row (recent auction week)
        const deleteIcon = this.page.locator(this.Auction_deleteIconFirstRow);
        await deleteIcon.waitFor({ state: 'visible', timeout: 5000 });
        await deleteIcon.click();
        // Wait for the modal popup and click Proceed
        const proceedButton = this.page.locator(this.Auction_proceedDeleteButton);
        await proceedButton.waitFor({ state: 'visible', timeout: 10000 });
        await proceedButton.click();
        // Wait for the loading modal to disappear and the complete purging modal to appear
        const purgeCompletedButton = this.page.locator(this.Auction_purgeCompletedButton);
        await purgeCompletedButton.waitFor({ state: 'visible', timeout: 60000 });
        await purgeCompletedButton.click();
        Logger("Recent auction week deleted and purged successfully.");
    }

    async ensureRoundOneIsOpen(): Promise<boolean> {
        const maxRetries = 2;        
        for (let attempt = 1; attempt <= maxRetries; attempt++) {
            Logger(`Attempt ${attempt}: Checking if Round One is open...`);            
            // Navigate to Scheduled Auctions page to check status
            const navigationBarPages = new NavMenuPage(this.page);
            await navigationBarPages.chooseSubNav_UnderSettingMainNav("Auctions Control Center");
            await this.page.waitForTimeout(2000);
            await navigationBarPages.clickAuctionControlTab("Scheduled Auctions");
            await this.page.waitForTimeout(2000);            
            // Check Round One status
            const statusLocator = this.page.locator(this.schedule_roundOneStatus);
            await statusLocator.waitFor({ state: 'visible', timeout: 5000 });
            const statusText = (await statusLocator.textContent())?.trim();
            Logger(`Round One status: ${statusText}`);            
            if (statusText === "Started") {
                Logger("Round One is already open.");
                return true;
            }            
            // If not open and this is not the last attempt, delete and recreate
            if (attempt < maxRetries) {
                Logger(`Round One is not open (status: ${statusText}). Retrying: deleting and recreating auction...`);
                await this.deleteRecentAuctionWeek();
                await this.createAuctionAndStartRoundOne();
            } else {
                Logger(`Round One is not open (status: ${statusText}) after ${maxRetries} attempts.`);
            }
        }        
        return false;
    }

    // -----------  Helpers ---------

    private async waitForRoundStatus(statusLocator: string, expectedStatus: string): Promise<void> {
        const timeout = 240000; // Default timeout of 4 minutes
        const pollInterval = 3000; // Default poll interval of 3 seconds       
        const navigationBarPages = new NavMenuPage(this.page);
        await navigationBarPages.chooseSubNav_UnderSettingMainNav("Auctions Control Center");
        await this.page.waitForTimeout(2000);
        await navigationBarPages.clickAuctionControlTab("Scheduled Auctions");
        await this.page.waitForTimeout(2000);
        const locator = this.page.locator(statusLocator);
        const start = Date.now();
        while (Date.now() - start < timeout) {
            const statusText = (await locator.textContent())?.trim();
            Logger(`Current status: ${statusText}`);
            if (statusText === expectedStatus) {
                Logger(`Status is now '${expectedStatus}'. Proceeding to next step.`);
                return;
            }
            await this.page.waitForTimeout(pollInterval);
        }
        throw new Error(`Timeout: Status did not change to '${expectedStatus}' within the expected time.`);
    }

    private async inputAuctionDateTimeFields(): Promise<void> {
        // --- Generate Current Date ---
        const now = new Date();
        const dateStr = this.formatDate(now);
        // Generate Time-From: current time + 2 mins
        const fromTime = new Date(now.getTime() + 2 * 60000);
        const fromTimeStr = this.formatTime(fromTime);
        // Generate R1 Time-To: Time-From + 10 mins
        const toTime = new Date(fromTime.getTime() + 10 * 60000);
        const toTimeStr = this.formatTime(toTime);
        // Generate R2 Time-To: Time-From + 30 mins
        const roundTwoToTime = new Date(fromTime.getTime() + 30 * 60000);
        const roundTwoToTimeStr = this.formatTime(roundTwoToTime);
        // Generate R3 Time-To: Time-From + 50 mins
        const roundThreeToTime = new Date(fromTime.getTime() + 50 * 60000);
        const roundThreeToTimeStr = this.formatTime(roundThreeToTime);
        // Input Round 1: DateTime-From (use current date)
        await this.fillDateTimeInput(this.Scheduling_RoundOneFromDateInput, dateStr);
        await this.fillDateTimeInput(this.Scheduling_RoundOneFromTimeInput, fromTimeStr);
        // Input Round 1: DateTime-To
        await this.fillDateTimeInput(this.Scheduling_RoundOneToDateInput, dateStr);
        await this.fillDateTimeInput(this.Scheduling_RoundOneToTimeInput, toTimeStr);
        // Input Round 2: DateTime-To
        await this.fillDateTimeInput(this.Scheduling_RoundTwoToDateInput, dateStr);
        await this.fillDateTimeInput(this.Scheduling_RoundTwoToTimeInput, roundTwoToTimeStr);
        // Input Round 3: DateTime-To
        await this.fillDateTimeInput(this.Scheduling_RoundThreeToDateInput, dateStr);
        await this.fillDateTimeInput(this.Scheduling_RoundThreeToTimeInput, roundThreeToTimeStr);
    }

    private formatDate(date: Date): string {
        const pad = (n: number) => n.toString().padStart(2, '0');
        return `${pad(date.getMonth() + 1)}/${pad(date.getDate())}/${date.getFullYear()}`;
    }

    private formatTime(date: Date): string {
        const pad = (n: number) => n.toString().padStart(2, '0');
        let hours = date.getHours();
        const minutes = pad(date.getMinutes());
        const period = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12 || 12;
        return `${pad(hours)}:${minutes} ${period}`;
    }

    private async fillDateTimeInput(locator: string, value: string): Promise<void> {
        const input = this.page.locator(locator);
        await input.click({ force: true });
        await input.fill('');
        await input.type(value, { delay: 100 });
        await this.page.keyboard.press('Tab');
        await this.page.waitForTimeout(1000);
    }
}

```
