# Microflow Analysis: SUB_CheckEmptyRecords

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$RMARequest_ImportHelperList** (A record of type: EcoATM_RMA.RMARequest_ImportHelper)

### Execution Steps:
1. **Take the list **$RMARequest_ImportHelperList**, perform a [FilterByExpression] where: { $currentObject/IMEISerial_Number = empty
or
trim($currentObject/IMEISerial_Number) = '' }, and call the result **$RMARequest_ImportHelper_EmptyReasonList****
2. **Decision:** "Al Reasons Available?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFile.InvalidReason] to: "length($RMARequest_ImportHelper_EmptyReasonList) + ' items are missing IMEIs, serial numbers or return reasons'"
      - **Save:** This change will be saved to the database immediately.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
