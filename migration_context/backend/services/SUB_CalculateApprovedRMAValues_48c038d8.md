# Microflow Analysis: SUB_CalculateApprovedRMAValues

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
2. **Take the list **$RMAItemList**, perform a [Filter] where: { EcoATM_RMA.ENUM_RMAItemStatus.Approve }, and call the result **$RMAItemList_Approved****
3. **Aggregate List
      - Store the result in a new variable called **$Count_Total****
4. **Decision:** "list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Run another process: "EcoATM_RMA.SUB_CalculateRMAApprovedSummary"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
