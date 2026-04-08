# Microflow Detailed Specification: ACT_BidDataDeleteSelected

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: AuctionUI.BidData)
- **$BidDataQueryHelper** (Type: AuctionUI.BidDataQueryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Delete**
2. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction = $BidDataQueryHelper/AuctionUI.BidDataQueryHelper_SchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BidDataQueryHelper/AuctionUI.BidDataQueryHelper_BuyerCode]` (Result: **$BidRound**)**
3. **Call Microflow **AuctionUI.ACT_Round3_SubmitBidData****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.