# Microflow Analysis: SUB_CreatePODetail

### Requirements (Inputs):
- **$IteratorMWPO_REPORT** (A record of type: EcoATM_PO.PODetails_NP)
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)
- **$BuyerCodeFromDB** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewPODetail**** ⚠️ *(This step has a safety catch if it fails)*
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
