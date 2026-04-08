# Microflow Analysis: SUB_SetBuyerCodeOwnerAndChanger

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Decision:** "existing buyer code?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.BuyerCode.EntityOwner] to: "$currentUser/Name
"
      - Change [EcoATM_BuyerManagement.BuyerCode.EntityChanger] to: "$currentUser/Name
"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
