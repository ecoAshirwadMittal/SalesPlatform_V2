# Microflow Analysis: DS_GetOrCreateAggregatedInventoryTotals

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SelectedWeek****
2. **Run another process: "AuctionUI.DS_GetOrCreateAggregatedInventoryTotalsByWeek"
      - Store the result in a new variable called **$AggreegatedInventoryTotal****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
