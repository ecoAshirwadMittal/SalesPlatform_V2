# Microflow Analysis: VAL_Buyer

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "name not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.BuyerEmptyValidationMessage] to: "empty
"**
4. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [CompanyName=$Buyer/CompanyName]
[id!=$Buyer]

 } (Call this list **$ExistingBuyer**)**
5. **Decision:** "Buyer does not exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.BuyerUniqueValidationMessage] to: "empty
"**
7. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
8. **Take the list **$BuyerCodeList**, perform a [Filter] where: { true }, and call the result **$BuyerCodeList_Delete****
9. **Take the list **$BuyerCodeList**, perform a [Subtract], and call the result **$BuyerCodeList_toCommit****
10. **Decision:** "Buyer codes not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
11. **Change Variable**
12. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.BuyerCodesEmptyValidationMessage] to: "'Atleast one buyer code should be added to buyer before saving'

"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
