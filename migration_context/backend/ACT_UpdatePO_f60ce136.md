# Microflow Analysis: ACT_UpdatePO

### Requirements (Inputs):
- **$POHelper** (A record of type: EcoATM_PO.POHelper)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper.ENUM_ActionType] to: "EcoATM_PO.ENUM_POActionType.Update
"
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InvalidFileValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InValidPOPeriod] to: "false
"**
2. **Retrieve
      - Store the result in a new variable called **$PurchaseOrder****
3. **Decision:** "PurchaseOrder exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
