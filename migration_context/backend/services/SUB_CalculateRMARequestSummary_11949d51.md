# Microflow Analysis: SUB_CalculateRMARequestSummary

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
6. **Search the Database for **EcoATM_RMA.RMAStatus** using filter: { [IsDefaultStatus] } (Call this list **$RMAStatus_Default**)**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.RequestSKUs] to: "$SKUs"
      - Change [EcoATM_RMA.RMA.RequestQty] to: "$Qty"
      - Change [EcoATM_RMA.RMA.RequestSalesTotal] to: "$SalesTotal"
      - Change [EcoATM_RMA.RMA.SystemStatus] to: "$RMAStatus_Default/SystemStatus"
      - Change [EcoATM_RMA.RMA_RMAStatus] to: "$RMAStatus_Default"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
