# Microflow Analysis: ACT_SchedulingAuction_SendAllBidsToSnowflake_Admin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Auction****
2. **Run another process: "AuctionUI.ACT_Auction_SendAllBidsToSnowflake_Admin"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
