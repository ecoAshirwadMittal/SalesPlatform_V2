# Microflow Detailed Specification: ACT_Start_CarryoverBids

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CarryOverBidsConfirmationPrompt'`**
2. **Create Variable **$Description** = `'Opening confirmation prompt for CarryOver for Buyer Code : '+$NP_BuyerCodeSelect_Helper/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Retrieve related **BidderRouterHelper_Week** via Association from **$BidderRouterHelper** (Result: **$CurrentWeek**)**
5. **Call Microflow **EcoATM_BidData.SUB_GetCurrentWeekMinusOne** (Result: **$LastWeek**)**
6. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$LastWeek/AuctionUI.Auction_Week and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true]` (Result: **$BidData_LastWeek**)**
7. 🔀 **DECISION:** `$BidData_LastWeek!=empty`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_BidData.PG_BidCarryover_Confirmation****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_BidData.No_CarrryoverBids_popup****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.