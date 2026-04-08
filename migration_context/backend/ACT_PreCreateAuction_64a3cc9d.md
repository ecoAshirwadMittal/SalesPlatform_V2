# Microflow Analysis: ACT_PreCreateAuction

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Run another process: "AuctionUI.GetOrCreateSchedulingAuctionHelper"
      - Store the result in a new variable called **$SchedulingAuction_Helper****
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
