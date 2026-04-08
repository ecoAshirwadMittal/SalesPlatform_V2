# Microflow Analysis: SUB_GetDADataFromExternalDB

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create List
      - Store the result in a new variable called **$DeviceAllocationList****
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Take the list **$DeviceAllocationList**, perform a [Find] where: { 'Total' }, and call the result **$NewAggregateRevenueTotal****
9. **Run another process: "EcoATM_DA.SUB_AggregateRevenueTotal_GetOrCreate"
      - Store the result in a new variable called **$AggregateRevenueTotal****
10. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.AgregateRevenueTotal.TotalPayout] to: "$NewAggregateRevenueTotal/Payout
"
      - Change [EcoATM_DA.AgregateRevenueTotal.TotalAvailableQty] to: "$NewAggregateRevenueTotal/AvailableQty
"
      - Change [EcoATM_DA.AgregateRevenueTotal.TotalSold] to: "$NewAggregateRevenueTotal/SalesQty
"
      - Change [EcoATM_DA.AgregateRevenueTotal.TotalRevenue] to: "$NewAggregateRevenueTotal/Revenue
"
      - Change [EcoATM_DA.AgregateRevenueTotal.TotalMargin] to: "$NewAggregateRevenueTotal/Margin
"
      - Change [EcoATM_DA.AgregateRevenueTotal.MarginPercentage] to: "$NewAggregateRevenueTotal/MarginPercentage
"
      - Change [EcoATM_DA.AgregateRevenueTotal.AverageSellingPrice] to: "$NewAggregateRevenueTotal/AvgSalesPrice
"
      - Change [EcoATM_DA.AgregateRevenueTotal.AveragePurchasePrice] to: "$NewAggregateRevenueTotal/Payout
"
      - **Save:** This change will be saved to the database immediately.**
11. **Delete**
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
