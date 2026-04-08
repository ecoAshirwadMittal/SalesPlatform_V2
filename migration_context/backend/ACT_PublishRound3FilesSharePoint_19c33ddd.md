# Microflow Analysis: ACT_PublishRound3FilesSharePoint

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "AuctionUI.DS_GetRoundThreeBuyers"
      - Store the result in a new variable called **$BuyerCodeList****
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
3. **Take the list **$SchedulingAuctionList**, perform a [FindByExpression] where: { $currentObject/Round=3 }, and call the result **$Round3SchedulingAuction****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
