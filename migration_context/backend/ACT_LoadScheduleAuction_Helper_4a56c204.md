# Microflow Analysis: ACT_LoadScheduleAuction_Helper

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Auction****
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
3. **Decision:** "list not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Run another process: "AuctionUI.ACT_Create_SchedulingAuction_Helper_Default"
      - Store the result in a new variable called **$NewSchedulingAuction_Helper_Auction_Exists****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
