# Microflow Detailed Specification: ACT_CalculateTargetPrice

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.ACT_ListHighBids** (Result: **$MaxLotBidList**)**
3. 🔀 **DECISION:** `$MaxLotBidList != empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_EB.SUB_RefreshEBPrice****
      2. **Call Microflow **AuctionUI.ACT_UpdateTargetPrice****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.