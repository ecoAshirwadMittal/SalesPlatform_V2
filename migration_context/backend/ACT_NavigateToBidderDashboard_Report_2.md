# Microflow Detailed Specification: ACT_NavigateToBidderDashboard_Report_2

### 📥 Inputs (Parameters)
- **$BuyerCodeSelectSearch_Helper** (Type: AuctionUI.BuyerCodeSelectSearch_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCodeSelectSearch_Helper** via Association from **$BuyerCodeSelectSearch_Helper** (Result: **$NP_BuyerCodeSelect_Helper**)**
2. **Close current page/popup**
3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper!= empty`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed' ) ]` (Result: **$Auction_Active_1**)**
      2. 🔀 **DECISION:** `$Auction_Active_1 = empty`
         ➔ **If [true]:**
            1. **Show Message (Information): `No active auction is present at this time.`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Retrieve related **Auction_Week** via Association from **$Auction_Active_1** (Result: **$CurrentAuction_1**)**
            2. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $CurrentAuction_1/WeekNumber -1 or WeekID = $CurrentAuction_1/WeekID -1 ) ]` (Result: **$PreviousAuction_1**)**
            3. **Create **AuctionUI.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **BuyerCode_Buyer** = `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/AuctionUI.BuyerCode_Buyer`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **Status** = `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/Status`
      - Set **BuyerCodeType** = `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType`**
            4. **Call Microflow **AuctionUI.ACT_GetBuyerBidDetailReportMenu****
            5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( Code = $BuyerCodeSelectSearch_Helper/SelectedCode ) ]` (Result: **$BuyerCode_2**)**
      2. 🔀 **DECISION:** `$BuyerCode_2 != empty`
         ➔ **If [false]:**
            1. **Show Message (Error): `Error: Buyer Code not found!`**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.Auction** Filter: `[ ( AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Started' or AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed' ) ]` (Result: **$Auction_Active**)**
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