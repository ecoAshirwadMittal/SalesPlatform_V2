# Microflow Analysis: ACT_GetActiveSchedulingAuction

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctions****
2. **Take the list **$SchedulingAuctions**, perform a [Filter] where: { AuctionUI.enum_SchedulingAuctionStatus.Started }, and call the result **$SchedulingAuctionStartedList****
3. **Take the list **$SchedulingAuctionStartedList**, perform a [Head], and call the result **$SchedulingAuctionStarted****
4. **Decision:** "? Started"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
