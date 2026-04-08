# Microflow Detailed Specification: ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round1

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Update **$BidderRouterHelper**
      - Set **BuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`**
3. **DB Retrieve **AuctionUI.Auction**  (Result: **$Auction_MostRecent**)**
4. 🔀 **DECISION:** `$Auction_MostRecent!= empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[ AuctionUI.SchedulingAuction_Auction = $Auction_MostRecent and Round = 1 ]` (Result: **$SchedulingAuction**)**
      2. 🔀 **DECISION:** `$SchedulingAuction!= empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/id = $SchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code = $NP_BuyerCodeSelect_Helper/Code ]` (Result: **$BidRound**)**
            2. 🔀 **DECISION:** `$BidRound != empty`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                  2. 🔀 **DECISION:** `$BidDataDoc != empty`
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DownloadFile**
                        2. **Call Microflow **Custom_Logging.SUB_Log_Info****
                        3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.