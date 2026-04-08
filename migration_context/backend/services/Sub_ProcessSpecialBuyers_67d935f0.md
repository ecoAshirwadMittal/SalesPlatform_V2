# Microflow Analysis: Sub_ProcessSpecialBuyers

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [isSpecialBuyer=true]
 } (Call this list **$BuyerList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
