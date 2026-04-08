# Microflow Analysis: SUB_ValidateRecords

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)
- **$RMARequest_ImportHelperList** (A record of type: EcoATM_RMA.RMARequest_ImportHelper)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "EcoATM_RMA.VAL_RMARequestFile"
      - Store the result in a new variable called **$RMAItemList****
2. **Decision:** "Valid File?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Take the list **$RMAItemList**, perform a [Filter] where: { empty }, and call the result **$RMAItemList_InvalidReason****
4. **Take the list **$RMAItemList**, perform a [Filter] where: { empty }, and call the result **$RMAItemList_InValidIMEI_List****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFile.InvalidReason] to: "length($RMAItemList_InValidIMEI_List) + ' items have invalid IMEIs or serial numbers'"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
