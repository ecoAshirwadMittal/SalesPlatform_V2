# Microflow Analysis: ACT_Round2BuyerCodes_SalesRep

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { Show everything } (Call this list **$BidRoundSelectionFilter**)**
2. **Search the Database for **AuctionUI.BuyerCode** using filter: { Show everything } (Call this list **$BuyerCodeList_all**)**
3. **Search the Database for **EcoATM_Buyer.BidData** using filter: { Show everything } (Call this list **$BidData_all**)**
4. **Search the Database for **AuctionUI.BuyerCode** using filter: { [EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData[
(BidAmount > 0)
and ( ( TargetPrice = 0 and BidAmount > 0)
or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent))
or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction/AuctionTitle]
 } (Call this list **$BuyerCodeList**)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
