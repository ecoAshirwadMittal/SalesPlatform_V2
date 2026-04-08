# Microflow Detailed Specification: ACT_NavigateToBidderDashboard_Report_3

### 📥 Inputs (Parameters)
- **$BuyerCode_3** (Type: AuctionUI.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$NP_BuyerCodeSelect_Helper/AuctionUI.NP_BuyerCodeSelect_Helper_BuyerCode`
      - Set **BuyerCodeSelect_Helper_SchedulingAuction** = `$NP_BuyerCodeSelect_Helper/AuctionUI.NP_BuyerCodeSelect_Helper_SchedulingAuction`**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
3. **Close current page/popup**
4. **Retrieve related **BuyerCodeSelect_Helper_BuyerCode** via Association from **$NewBuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
5. 🔀 **DECISION:** `$BuyerCode != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.Bidder_Dashboard****
      2. **Call Microflow **AuctionUI.ACT_OpenBidderDashboard****
      3. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed' ) ]` (Result: **$Auction_Active_1**)**
      4. 🔀 **DECISION:** `$Auction_Active_1 = empty`
         ➔ **If [true]:**
            1. **Show Message (Information): `No active auction is present at this time.`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Retrieve related **Auction_Week** via Association from **$Auction_Active_1** (Result: **$CurrentAuction_1**)**
            2. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $CurrentAuction_1/WeekNumber -1 or WeekID = $CurrentAuction_1/WeekID -1 ) ]` (Result: **$PreviousAuction_1**)**
            3. **Call Microflow **AuctionUI.ACT_GetBuyerBidDetailReportMenu****
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( Code = $NewBuyerCodeSelect_Helper/Code ) ]` (Result: **$BuyerCode_2**)**
      2. 🔀 **DECISION:** `$BuyerCode_2 != empty`
         ➔ **If [false]:**
            1. **Show Message (Error): `Error: Buyer Code not found!`**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed' ) ]` (Result: **$Auction_Active**)**
            2. 🔀 **DECISION:** `$Auction_Active = empty`
               ➔ **If [true]:**
                  1. **Show Message (Information): `No active auction is present at this time.`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Retrieve related **Auction_Week** via Association from **$Auction_Active** (Result: **$CurrentAuction**)**
                  2. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $CurrentAuction/WeekNumber -1 or WeekID = $CurrentAuction/WeekID -1 ) ]` (Result: **$PreviousAuction**)**
                  3. **Call Microflow **AuctionUI.ACT_GetBuyerBidDetailReportMenu****
                  4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.