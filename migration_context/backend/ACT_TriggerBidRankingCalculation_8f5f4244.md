# Microflow Analysis: ACT_TriggerBidRankingCalculation

### Requirements (Inputs):
- **$ResetRanking** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Search the Database for **AuctionUI.BidRanking** using filter: { Show everything } (Call this list **$BidRanking**)**
4. **Decision:** "BidRanking exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Show Message**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
