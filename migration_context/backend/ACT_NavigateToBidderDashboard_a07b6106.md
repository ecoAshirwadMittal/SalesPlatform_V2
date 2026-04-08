# Microflow Analysis: ACT_NavigateToBidderDashboard

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Decision:** "Non PWS?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **BuyerCode Available?**
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
4. **Close Form**
5. **Create Object
      - Store the result in a new variable called **$BuyerCodeSelect_Helper****
6. **Run another process: "AuctionUI.ACT_BidderDashboardNavigationHelper"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
