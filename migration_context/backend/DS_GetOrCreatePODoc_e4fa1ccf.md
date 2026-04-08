# Microflow Analysis: DS_GetOrCreatePODoc

### Requirements (Inputs):
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$PurchaseOrderDoc****
2. **Decision:** "PurchaseOrderDoc exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
