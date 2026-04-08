# Microflow Analysis: SUB_SelectFirstPWSBuyerCode

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GET_CurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
2. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
3. **Retrieve
      - Store the result in a new variable called **$BuyerList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Take the list **$BuyerCodeList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_BuyerManagement.BuyerCode_Session = empty
or
$currentObject/EcoATM_BuyerManagement.BuyerCode_Session = $currentSession }, and call the result **$BuyerCodeList_Available****
6. **Take the list **$BuyerCodeList_Available**, perform a [Head], and call the result **$BuyerCode****
7. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.BuyerCode_Session] to: "$currentSession"
      - **Save:** This change will be saved to the database immediately.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
