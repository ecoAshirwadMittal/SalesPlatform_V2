# Microflow Analysis: SUB_CheckLastWeekBidDataExists

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Log Message**
2. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true] } (Call this list **$BidData**)**
3. **Log Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
