# Microflow Analysis: ACT_Round2BuyerCodes_noXPATH

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [Find] where: { 1 }, and call the result **$SchedulingAuctionRound1****
3. **Log Message**
4. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
5. **Create List
      - Store the result in a new variable called **$EligibleBuyerCodeList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
