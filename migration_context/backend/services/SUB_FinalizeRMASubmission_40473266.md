# Microflow Analysis: SUB_FinalizeRMASubmission

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$RMAItemList** (A record of type: EcoATM_RMA.RMAItem)

### Execution Steps:
1. **Create Variable**
2. **Retrieve
      - Store the result in a new variable called **$RMAId****
3. **Decision:** "RMAId Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAId.MaxRMAId] to: "$RMAId/MaxRMAId + 1"
      - **Save:** This change will be saved to the database immediately.**
5. **Change Variable**
6. **Run another process: "EcoATM_RMA.SUB_CalculateRMARequestSummary"**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.Number] to: "$RMARequestId"
      - Change [EcoATM_RMA.RMA.SubmittedDate] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
8. **Permanently save **$undefined** to the database.**
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "EcoATM_RMA.SUB_SendEmail_RMASubmitted"**
11. **Run another process: "EcoATM_RMA.SUB_SendRMADetailsToSnowflake"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
