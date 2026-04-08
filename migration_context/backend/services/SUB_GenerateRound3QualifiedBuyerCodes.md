# Microflow Detailed Specification: SUB_GenerateRound3QualifiedBuyerCodes

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RetrieveRound3QualifiedBuyers'`**
2. **Create Variable **$Description** = `'List Buyers Qualified For Round 3'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **JavaCallAction**
5. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[AuctionUI.BidData_BuyerCode/AuctionUI.BidData[BidAmount > 0]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true] /AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$BuyerCodeList_Qualified**)**
6. **Call Microflow **AuctionUI.SUB_GenerateQualifiedBuyerCodes** (Result: **$BuyerCodeList**)**
7. **JavaCallAction**
8. 🏁 **END:** Return `$BuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.