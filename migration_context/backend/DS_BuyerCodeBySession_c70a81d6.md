# Microflow Analysis: DS_BuyerCodeBySession

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
2. **Take the list **$BuyerCodeList**, perform a [Filter] where: { AuctionUI.enum_BuyerCodeType.Premium_Wholesale }, and call the result **$PWSBuyerCodeList****
3. **Take the list **$PWSBuyerCodeList**, perform a [Head], and call the result **$BuyerCode****
4. **Decision:** "BuyerCode Available?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
