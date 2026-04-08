# Microflow Analysis: SUB_ListRoundThreeBuyersDataForQualifiedBuyers

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [Filter] where: { 3 }, and call the result **$SchedulingAuctionList_Round3****
3. **Take the list **$SchedulingAuctionList_Round3**, perform a [Head], and call the result **$SchedulingAuction_Round3****
4. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodesList****
5. **Take the list **$QualifiedBuyerCodesList**, perform a [Filter] where: { true }, and call the result **$QualifiedBuyerCodesList_Included****
6. **Create List
      - Store the result in a new variable called **$RoundThreeBuyersDataReport_NP****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
