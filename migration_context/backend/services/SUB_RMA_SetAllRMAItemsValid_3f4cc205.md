# Microflow Analysis: SUB_RMA_SetAllRMAItemsValid

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
2. **Take the list **$RMAItemList**, perform a [Find] where: { empty }, and call the result **$RMAItem_StatusEmpty****
3. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.AllRMAItemsValid] to: "false"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
