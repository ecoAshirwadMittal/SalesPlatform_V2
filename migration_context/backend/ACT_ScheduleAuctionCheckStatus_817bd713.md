# Microflow Analysis: ACT_ScheduleAuctionCheckStatus

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.ACT_SetAuctionScheduleClosed"**
3. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Run another process: "AuctionUI.ACT_SetAuctionScheduleStarted"**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
