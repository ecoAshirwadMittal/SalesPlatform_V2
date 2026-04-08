# Microflow Analysis: SUB_CalculateBuyerDetailsTotal

### Requirements (Inputs):
- **$BuyerSummary** (A record of type: EcoATM_DA.BuyerSummary)

### Execution Steps:
1. **Run another process: "EcoATM_DA.SUB_BuyerDetailsTotal_GetOrCreate"
      - Store the result in a new variable called **$BuyerDetailsTotals****
2. **Retrieve
      - Store the result in a new variable called **$BuyerDetailsList****
3. **Aggregate List
      - Store the result in a new variable called **$SumSalesQty****
4. **Aggregate List
      - Store the result in a new variable called **$SumAmount****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.BuyerDetailTotals.SalesQty] to: "$SumSalesQty
"
      - Change [EcoATM_DA.BuyerDetailTotals.Amount] to: "$SumAmount
"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
