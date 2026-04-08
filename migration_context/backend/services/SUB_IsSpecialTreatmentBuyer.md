# Microflow Detailed Specification: SUB_IsSpecialTreatmentBuyer

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. 🔀 **DECISION:** `$BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/isSpecialBuyer = true`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      3. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round = 2]` (Result: **$BidRoundSelectionFilter**)**
      2. 🔀 **DECISION:** `$BidRoundSelectionFilter/STBAllowAllBuyersOverride = true`
         ➔ **If [false]:**
            1. **Retrieve related **BuyerCode_Buyer** via Association from **$BuyerCode** (Result: **$Buyer**)**
            2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round < $SchedulingAuction/Round]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Auction] [AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer = $Buyer]` (Result: **$BidDataList**)**
            3. **AggregateList**
            4. 🔀 **DECISION:** `$BidDataCount = 0`
               ➔ **If [true]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
                  3. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
                  3. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
            3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.