# Microflow Analysis: VAL_BuyerCode_PO

### Requirements (Inputs):
- **$PODetailsList** (A record of type: EcoATM_PO.PODetails_NP)
- **$POHelper** (A record of type: EcoATM_PO.POHelper)

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Create Variable**
4. **Create List
      - Store the result in a new variable called **$MissingBuyerCodeList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeList] to: "$MissingBuyerCodes
"**
7. **Log Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
