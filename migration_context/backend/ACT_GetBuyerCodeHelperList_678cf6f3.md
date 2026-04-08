# Microflow Analysis: ACT_GetBuyerCodeHelperList

### Requirements (Inputs):
- **$BuyerCode_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.NewBuyerHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$NewBuyerCodeHelperList****
2. **Take the list **$NewBuyerCodeHelperList**, perform a [FilterByExpression] where: { $currentObject/BuyerCodeType = $BuyerCode_Helper/BuyerCodeType }, and call the result **$FilteredBuyerCodeHelperList****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
