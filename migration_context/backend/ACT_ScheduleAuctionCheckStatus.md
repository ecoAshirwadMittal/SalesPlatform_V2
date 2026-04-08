# Microflow Detailed Specification: ACT_ScheduleAuctionCheckStatus

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.ACT_SetAuctionScheduleClosed****
3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **Call Microflow **AuctionUI.ACT_SetAuctionScheduleStarted****
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.