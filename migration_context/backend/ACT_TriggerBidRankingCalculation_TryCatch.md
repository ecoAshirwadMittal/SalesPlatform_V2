# Microflow Detailed Specification: ACT_TriggerBidRankingCalculation_TryCatch

### 📥 Inputs (Parameters)
- **$ResetRanking** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation** (Result: **$Response**)**
2. 🔀 **DECISION:** `$Response`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.