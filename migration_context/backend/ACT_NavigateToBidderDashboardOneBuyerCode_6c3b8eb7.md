# Microflow Analysis: ACT_NavigateToBidderDashboardOneBuyerCode

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
2. **Close Form**
3. **Run another process: "AuctionUI.ACT_BidderDashboardNavigationHelper"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
