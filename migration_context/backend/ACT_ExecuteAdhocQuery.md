# Microflow Detailed Specification: ACT_ExecuteAdhocQuery

### 📥 Inputs (Parameters)
- **$BidDataDeleteHelper** (Type: AuctionUI.BidDataDeleteHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.SUB_Execute_CleanStep****
3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
4. **Show Message (Information): `Script Execution completed`**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.