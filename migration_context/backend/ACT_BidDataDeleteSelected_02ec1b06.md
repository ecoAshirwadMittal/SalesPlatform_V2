# Microflow Analysis: ACT_BidDataDeleteSelected

### Requirements (Inputs):
- **$BidDataList** (A record of type: AuctionUI.BidData)
- **$BidDataQueryHelper** (A record of type: AuctionUI.BidDataQueryHelper)

### Execution Steps:
1. **Delete**
2. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction = $BidDataQueryHelper/AuctionUI.BidDataQueryHelper_SchedulingAuction
and
AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BidDataQueryHelper/AuctionUI.BidDataQueryHelper_BuyerCode] } (Call this list **$BidRound**)**
3. **Run another process: "AuctionUI.ACT_Round3_SubmitBidData"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
