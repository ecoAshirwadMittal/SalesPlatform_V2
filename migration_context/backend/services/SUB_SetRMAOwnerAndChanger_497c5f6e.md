# Microflow Analysis: SUB_SetRMAOwnerAndChanger

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Decision:** "new RMA?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.EntityOwner] to: "$currentUser/Name"
      - Change [EcoATM_RMA.RMA.EntityChanger] to: "$currentUser/Name"
      - Change [EcoATM_RMA.RMA.SystemStatus] to: "$RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/SystemStatus"**
3. **Permanently save **$undefined** to the database.**
4. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
5. **Run another process: "EcoATM_RMA.SUB_SetRMAItemOwnerAndChanger"
      - Store the result in a new variable called **$RMAItemList_toCommit****
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
