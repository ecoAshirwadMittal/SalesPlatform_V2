# Microflow Analysis: SUB_Round2_DeleteUnsubmittedBids

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRound2** (A record of type: Object)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$BidData_ToDelete****
2. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction]
 } (Call this list **$BidRound**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
