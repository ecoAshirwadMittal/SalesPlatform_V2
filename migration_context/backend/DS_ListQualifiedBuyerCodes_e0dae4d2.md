# Microflow Analysis: DS_ListQualifiedBuyerCodes

### Requirements (Inputs):
- **$QualifiedBuyerCodesQueryHelper** (A record of type: EcoATM_BuyerManagement.QualifiedBuyerCodesQueryHelper)

### Execution Steps:
1. **Decision:** "Not Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
3. **Search the Database for **EcoATM_BuyerManagement.QualifiedBuyerCodes** using filter: { [
  (
    EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction = $SchedulingAuction
  )
] } (Call this list **$QualifiedBuyerCodesList**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
