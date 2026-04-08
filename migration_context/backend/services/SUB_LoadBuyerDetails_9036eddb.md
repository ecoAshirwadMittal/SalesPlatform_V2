# Microflow Analysis: SUB_LoadBuyerDetails

### Requirements (Inputs):
- **$CurrentBuyerSummary** (A record of type: EcoATM_DA.BuyerSummary)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Search the Database for **EcoATM_DA.BuyerDetail** using filter: { [
EcoATM_DA.BuyerDetail_BuyerSummary = $CurrentBuyerSummary
]
 } (Call this list **$ExistingBuyerDetails**)**
2. **Delete**
3. **Create Variable**
4. **Import Xml**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Run another process: "EcoATM_Reports.SUB_CalculateBuyerDetailsTotal"**
7. **Log Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
