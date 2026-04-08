# Microflow Analysis: ACT_Round2BuyerCodes

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { Show everything } (Call this list **$BidRoundSelectionFilter**)**
3. **Search the Database for **AuctionUI.BuyerCode** using filter: { [EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData[
(BidAmount > 0)
and ( ( TargetPrice = 0 and BidAmount > 0)
or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent))
or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction/AuctionTitle]
 } (Call this list **$BuyerCodeList**)**
4. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer[AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name=$currentUser/Name]] } (Call this list **$BuyerCodeList_UserAssigned**)**
5. **Take the list **$BuyerCodeList**, perform a [Intersect], and call the result **$BuyerCodeList_Intersection****
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
