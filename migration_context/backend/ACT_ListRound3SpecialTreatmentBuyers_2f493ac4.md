# Microflow Analysis: ACT_ListRound3SpecialTreatmentBuyers

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [Filter] where: { 3 }, and call the result **$SchedulingAuctionFiltered****
3. **Take the list **$SchedulingAuctionFiltered**, perform a [Head], and call the result **$SchedulingAuctionRound3****
4. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [
  (
    isSpecialBuyer = true()
    and Status = 'Active'
  )
] } (Call this list **$BuyerList_IsSpecialTreatment**)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
