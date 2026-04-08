# Microflow Analysis: SUB_SetBuyerOwnerAndChanger

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Decision:** "existing buyer?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.EntityOwner] to: "$currentUser/Name
"
      - Change [EcoATM_BuyerManagement.Buyer.EntityChanger] to: "$currentUser/Name
"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
