# Microflow Analysis: SUB_GetOrCreatePOHelper

### Execution Steps:
1. **Search the Database for **Administration.Account** using filter: { [id = $currentUser] } (Call this list **$Account**)**
2. **Retrieve
      - Store the result in a new variable called **$POHelper****
3. **Decision:** "POHelper exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper.ENUM_ActionType] to: "empty
"
      - Change [EcoATM_PO.POHelper.EnablePOUpdate] to: "false
"
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InvalidFileValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InValidPOPeriod] to: "false
"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
