# Microflow Analysis: VAL_ValidateBuyerCode_PreSave

### Requirements (Inputs):
- **$NewBuyerHelper** (A record of type: EcoATM_BuyerManagement.NewBuyerHelper)
- **$NewCode** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
3. **Take the list **$BuyerCodeList**, perform a [Filter] where: { $NewCode }, and call the result **$NewBuyerCodeList****
4. **Take the list **$NewBuyerCodeList**, perform a [Head], and call the result **$NewBuyerCode****
5. **Decision:** "exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Show Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
