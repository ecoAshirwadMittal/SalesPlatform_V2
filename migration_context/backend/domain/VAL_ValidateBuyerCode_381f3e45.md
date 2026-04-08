# Microflow Analysis: VAL_ValidateBuyerCode

### Requirements (Inputs):
- **$BuyerCode_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCode_Helper)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    Code = $BuyerCode_Helper/Code
  )
] } (Call this list **$BuyerCodeList_Existing**)**
3. **Take the list **$BuyerCodeList_Existing**, perform a [Head], and call the result **$ExistingBuyerCode****
4. **Decision:** "exists?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
