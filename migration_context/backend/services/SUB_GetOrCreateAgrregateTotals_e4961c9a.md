# Microflow Analysis: SUB_GetOrCreateAgrregateTotals

### Requirements (Inputs):
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "AuctionUI.DS_GetOrCreateAggregatedInventoryTotalsByWeek"
      - Store the result in a new variable called **$AggreegatedInventoryTotal****
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggreegatedInventoryTotals.AvgTargetPrice] to: "$AggregatedInventory/AvgTargetPrice
"
      - Change [AuctionUI.AggreegatedInventoryTotals.AvgPayout] to: "$AggregatedInventory/AvgPayout
"
      - Change [AuctionUI.AggreegatedInventoryTotals.TotalPayout] to: "$AggregatedInventory/TotalPayout
"
      - Change [AuctionUI.AggreegatedInventoryTotals.DWAvgTargetPrice] to: "$AggregatedInventory/DWAvgTargetPrice
"
      - Change [AuctionUI.AggreegatedInventoryTotals.DWAvgPayout] to: "$AggregatedInventory/DWAvgPayout
"
      - Change [AuctionUI.AggreegatedInventoryTotals.DWTotalQuantity] to: "$AggregatedInventory/DWTotalQuantity
"
      - Change [AuctionUI.AggreegatedInventoryTotals.TotalQuantity] to: "$AggregatedInventory/TotalQuantity
"
      - Change [AuctionUI.AggreegatedInventoryTotals.DWTotalPayout] to: "$AggregatedInventory/DWTotalPayout
"
      - **Save:** This change will be saved to the database immediately.**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
