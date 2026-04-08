# Microflow Detailed Specification: SUB_AssignRoundTwoBuyers

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'AssignRoundTwoBuyers'`**
2. **Create Variable **$Description** = `'List Buyers Qualified For Round 2'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
5. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/CalculateRound2BuyerParticipation = true`
   ➔ **If [true]:**
      1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
      2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Round = 2` (Result: **$SchedulingAuction**)**
      3. **Call Microflow **AuctionUI.SUB_GenerateRound2QualifiedBuyerCodes** (Result: **$BuyerCodeList**)**
      4. **Commit/Save **$Auction** to Database**
      5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.