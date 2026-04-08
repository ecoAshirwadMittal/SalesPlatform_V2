# Microflow Analysis: SUB_CreateBuyerCodeSelectHelper

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_HelperList** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_BuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Aggregate List
      - Store the result in a new variable called **$Count_BuyerCodes****
2. **Decision:** "count=1?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Take the list **$NP_BuyerCodeSelect_HelperList**, perform a [FindByExpression] where: { $currentObject/Code != empty }, and call the result **$oneBuyerCode****
4. **Run another process: "AuctionUI.ACT_NavigateToBidderDashboardOneBuyerCode"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
