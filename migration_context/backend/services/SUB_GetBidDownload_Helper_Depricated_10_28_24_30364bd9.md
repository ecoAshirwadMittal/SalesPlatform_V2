# Microflow Analysis: SUB_GetBidDownload_Helper_Depricated_10_28_24

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$ClickedRound** (A record of type: AuctionUI.ClickedRound)
- **$NewBidDataDoc** (A record of type: AuctionUI.BidDataDoc)

### Execution Steps:
1. **Log Message**
2. **Run another process: "AuctionUI.ACT_GetMostRecentAuction"
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
4. **Take the list **$SchedulingAuctionList**, perform a [Filter] where: { $ClickedRound/Round }, and call the result **$NewSchedulingAuctionList****
5. **Take the list **$NewSchedulingAuctionList**, perform a [Head], and call the result **$CurrentSchedulingAuction****
6. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
7. **Take the list **$BidRoundList**, perform a [Filter] where: { $NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode }, and call the result **$CurrentBidRoundList****
8. **Take the list **$CurrentBidRoundList**, perform a [Head], and call the result **$NewBidRound****
9. **Update the **$undefined** (Object):
      - Change [EcoATM_Caching.NP_BuyerCodeSelect_Helper_BidRound] to: "$NewBidRound"**
10. **Run another process: "EcoATM_Buyer.SUB_CreateBidDownload_Helper_Depricated_10_28_24"
      - Store the result in a new variable called **$BidDownload_HelperList****
11. **Log Message**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
