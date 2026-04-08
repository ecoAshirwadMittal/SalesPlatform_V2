# Microflow Analysis: SUB_GetAggregateTotals

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Execute Database Query
      - Store the result in a new variable called **$AggregatedInventoryTotals****
2. **Take the list **$AggregatedInventoryTotals**, perform a [Head], and call the result **$NewAggregatedInventoryTotals****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
