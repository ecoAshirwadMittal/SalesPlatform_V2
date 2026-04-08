# Microflow Analysis: ACT_ExecuteAdhocQuery

### Requirements (Inputs):
- **$BidDataDeleteHelper** (A record of type: AuctionUI.BidDataDeleteHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
3. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
