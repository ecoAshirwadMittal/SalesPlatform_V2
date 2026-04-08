# Microflow Analysis: DS_GetRoundThreeBuyers

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [Find] where: { 3 }, and call the result **$SchedulingAuction****
3. **Decision:** "round 3"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList_Round3****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
