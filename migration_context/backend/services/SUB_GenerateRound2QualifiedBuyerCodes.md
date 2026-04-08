# Microflow Detailed Specification: SUB_GenerateRound2QualifiedBuyerCodes

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RetrieveRound2QualifiedBuyers'`**
2. **Create Variable **$Description** = `'List Buyers Qualified For Round 2'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **AuctionUI.ACT_ListRound2BuyerCodesUsingAE** (Result: **$BuyerCodeList_Round2**)**
5. **Call Microflow **AuctionUI.SUB_GenerateQualifiedBuyerCodes** (Result: **$BuyerCodeList**)**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
7. 🏁 **END:** Return `$BuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.