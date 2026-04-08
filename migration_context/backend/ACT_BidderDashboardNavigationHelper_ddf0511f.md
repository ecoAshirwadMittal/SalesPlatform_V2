# Microflow Analysis: ACT_BidderDashboardNavigationHelper

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Decision:** "Not Premium Wholesale Buyer?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "AuctionUI.ACT_OpenBidderDashboard"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
