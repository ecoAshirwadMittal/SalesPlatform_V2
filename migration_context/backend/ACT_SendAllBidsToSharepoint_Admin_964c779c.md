# Microflow Analysis: ACT_SendAllBidsToSharepoint_Admin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Log Message**
2. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
3. **Take the list **$BidRoundList**, perform a [Filter] where: { true }, and call the result **$NewBidRoundList_submitted****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Log Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
