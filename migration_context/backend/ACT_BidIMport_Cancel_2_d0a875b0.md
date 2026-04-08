# Microflow Analysis: ACT_BidIMport_Cancel_2

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (A record of type: AuctionUI.BuyerCodeSelect_Helper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_Caching.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Close Form**
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
3. **Run another process: "AuctionUI.SUB_AuctionTimerHelper"
      - Store the result in a new variable called **$AuctionTimerHelper****
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
