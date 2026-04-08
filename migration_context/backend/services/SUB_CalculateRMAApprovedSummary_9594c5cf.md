# Microflow Analysis: SUB_CalculateRMAApprovedSummary

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAItemList** (A record of type: EcoATM_RMA.RMAItem)

### Execution Steps:
1. **Aggregate List
      - Store the result in a new variable called **$Qty****
2. **Create Variable**
3. **Create List
      - Store the result in a new variable called **$DistinctSKUs****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Aggregate List
      - Store the result in a new variable called **$SKUs****
6. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.ApprovedSKUs] to: "$SKUs"
      - Change [EcoATM_RMA.RMA.ApprovedQty] to: "$Qty"
      - Change [EcoATM_RMA.RMA.ApprovedSalesTotal] to: "$SalesTotal"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
