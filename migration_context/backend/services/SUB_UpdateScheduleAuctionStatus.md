# Microflow Detailed Specification: SUB_UpdateScheduleAuctionStatus

### 📥 Inputs (Parameters)
- **$SchedulingAuctionStatus** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[id = $SchedulingAuction]` (Result: **$SchedulingAuctionToUpdate**)**
3. **Update **$SchedulingAuctionToUpdate** (and Save to DB)
      - Set **RoundStatus** = `$SchedulingAuctionStatus`**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.