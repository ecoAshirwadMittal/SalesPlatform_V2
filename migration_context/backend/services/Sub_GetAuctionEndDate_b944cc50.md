# Microflow Analysis: Sub_GetAuctionEndDate

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [Sort], and call the result **$SortedSchedulingAuctionList****
3. **Take the list **$SortedSchedulingAuctionList**, perform a [FindByExpression] where: { $currentObject/HasRound=true }, and call the result **$LastSchedulingAuction****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
