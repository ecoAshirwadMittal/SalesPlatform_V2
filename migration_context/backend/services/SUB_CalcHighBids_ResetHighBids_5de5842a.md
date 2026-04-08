# Microflow Analysis: SUB_CalcHighBids_ResetHighBids

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound[AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$Auction]
and HighestBid = true] } (Call this list **$BidDataList_AuctionHighBids**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
