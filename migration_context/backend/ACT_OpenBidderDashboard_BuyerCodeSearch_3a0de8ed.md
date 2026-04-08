# Microflow Analysis: ACT_OpenBidderDashboard_BuyerCodeSearch

### Requirements (Inputs):
- **$BuyerCodeSelectSearch_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCodeSelectSearchHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$NP_BuyerCodeSelect_Helper****
2. **Retrieve
      - Store the result in a new variable called **$Parent_NPBuyerCodeSelectHelper****
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
4. **Run another process: "AuctionUI.ACT_BidderDashboardNavigationHelper"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
