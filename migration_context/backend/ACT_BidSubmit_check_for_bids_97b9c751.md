# Microflow Analysis: ACT_BidSubmit_check_for_bids

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidDataList****
2. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { $currentObject/BidAmount != empty and $currentObject/BidAmount > 0 }, and call the result **$BidDataList_GreaterThanZero****
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Decision:** "Bid Data exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
