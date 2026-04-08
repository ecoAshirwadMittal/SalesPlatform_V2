# Microflow Analysis: ACT_ShowScheduleAuctionConfirmation

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Run another process: "AuctionUI.VAL_Schedule_Auction"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Show Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
