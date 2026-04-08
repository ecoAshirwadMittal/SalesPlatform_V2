# Microflow Analysis: VAL_BuyerCode

### Requirements (Inputs):
- **$BuyerCodeList** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Create Variable**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Take the list **$BuyerCodeList**, perform a [Filter] where: { false }, and call the result **$BuyerCode_CodeEmptyValid****
4. **Decision:** "list empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.buyerCodeInvalidMessage_empty] to: "empty
"**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Take the list **$BuyerCodeList**, perform a [Filter] where: { false }, and call the result **$BuyerCde_CodeUniqueValid****
8. **Decision:** "list empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.buyerCodeInvalidMessage_Unique] to: "empty
"**
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Take the list **$BuyerCodeList**, perform a [Filter] where: { false }, and call the result **$BuyeCode_TypeValid****
12. **Decision:** "list empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
13. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.buyerCodeTypeInvalidMessage] to: "empty
"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
