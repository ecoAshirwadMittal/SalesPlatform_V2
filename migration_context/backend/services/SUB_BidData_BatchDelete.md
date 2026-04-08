# Microflow Detailed Specification: SUB_BidData_BatchDelete

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction and AuctionUI.BidData_BidRound/AuctionUI.BidRound[ AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer = $Buyer] ]` (Result: **$BidDataList_Buyer**)**
3. 🔀 **DECISION:** `$BidDataList_Buyer != empty`
   ➔ **If [true]:**
      1. **AggregateList**
      2. **Create Variable **$Amount** = `1000`**
      3. **Create Variable **$TotalProcessed** = `0`**
      4. **Create Variable **$Offset** = `0`**
      5. **JavaCallAction**
      6. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction and AuctionUI.BidData_BidRound/AuctionUI.BidRound[ AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer = $Buyer] ]` (Result: **$BidDataList**)**
      7. **AggregateList**
      8. **Update Variable **$TotalProcessed** = `$TotalProcessed + $RetrievedBidDataCount`**
      9. **Delete**
      10. **JavaCallAction**
      11. **LogMessage**
      12. 🔀 **DECISION:** `$TotalProcessed >= $TotalItems`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
               *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.